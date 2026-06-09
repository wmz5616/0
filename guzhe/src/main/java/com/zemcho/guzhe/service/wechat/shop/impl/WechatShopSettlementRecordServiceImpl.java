package com.zemcho.guzhe.service.wechat.shop.impl;

import com.alibaba.excel.write.handler.WriteHandler;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.wechat.shop.param.SettlementRecordSearchParam;
import com.zemcho.guzhe.controller.wechat.shop.vo.SettlementRecordItemVo;
import com.zemcho.guzhe.controller.wechat.shop.vo.SettlementRecordListVo;
import com.zemcho.guzhe.controller.wechat.shop.vo.SettlementRecordSummaryVo;
import com.zemcho.guzhe.entity.shop.Shop;
import com.zemcho.guzhe.mapper.shop.ShopMapper;
import com.zemcho.guzhe.mapper.sys.TransactionFlowMapper;
import com.zemcho.guzhe.service.shop.ShopManagerService;
import com.zemcho.guzhe.service.wechat.shop.WechatShopSettlementRecordService;
import com.zemcho.guzhe.util.excel.ExcelUtil;
import com.zemcho.guzhe.util.excel.SettlementRecordHeaderHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

/**
 * 小程序商家端结算记录服务实现
 */
@Service
public class WechatShopSettlementRecordServiceImpl implements WechatShopSettlementRecordService {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal PERCENT_DIVISOR = new BigDecimal("100");

    @Autowired
    private TransactionFlowMapper transactionFlowMapper;

    @Autowired
    private ShopManagerService shopManagerService;

    @Autowired
    private ShopMapper shopMapper;

    @Value("${tbg.handling.rate}")
    private BigDecimal handlingRate;

    @Override
    public Result lists(SettlementRecordSearchParam param, String token) {
        // 1. 参数校验：验证搜索参数的合法性（如时间范围、店铺ID等）
        Result validateResult = validateParam(param);
        if (!validateResult.success()) {
            return validateResult;
        }

        // 2. 权限校验：验证当前用户是否有权限查看该店铺的结算记录
        // 是否是该商家的管理者
        Result authResult = shopManagerService.checkWechatUserIsShopManager(token, param.getShopId());
        if (!authResult.success()) {
            return authResult;
        }

        // 3. 获取汇总数据：查询结算记录的统计数据（如总金额、总笔数）
        SettlementRecordSummaryVo summary = getSummary(param);
        // 4. 查询总数：获取符合条件的结算记录总条数（用于分页）
        long total = transactionFlowMapper.selectWechatSettlementRecordListCount(param);

        // 5. 开启分页：使用PageHelper设置分页参数（pageNum, pageSize, count=false表示不自动count）
        PageHelper.startPage(param.getPageNum(), param.getPageSize(), false);
        // 6. 查询列表数据：获取当前页的结算记录列表
        List<SettlementRecordItemVo> list = transactionFlowMapper.selectWechatSettlementRecordList(param);
        // 7. 数据增强：补充列表中每条记录的额外信息（如状态描述、格式化时间等）
        enrichList(list);

        // 8. 构建分页对象：将列表数据包装成PageInfo对象
        PageInfo<SettlementRecordItemVo> pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(total);

        // 9. 组装返回结果：创建响应对象，填充汇总数据和分页数据
        SettlementRecordListVo result = new SettlementRecordListVo();
        result.setTotalCount(summary.getTotalCount());
        result.setTotalAmount(formatAmount(summary.getTotalAmount()));
        result.setPageInfo(pageInfo);
        return Result.success("获取成功", result);
    }

    @Override
    public void export(SettlementRecordSearchParam param, String token, HttpServletResponse response) {
        Result validateResult = validateParam(param);
        if (!validateResult.success()) {
            throw new IllegalArgumentException(validateResult.getMsg());
        }

        Result authResult = shopManagerService.checkWechatUserIsShopManager(token, param.getShopId());
        if (!authResult.success()) {
            throw new IllegalArgumentException(authResult.getMsg());
        }

        Shop shop = shopMapper.selectById(param.getShopId());
        if (shop == null) {
            throw new IllegalArgumentException("商家不存在");
        }

        SettlementRecordSummaryVo summary = getSummary(param);
        List<SettlementRecordItemVo> list = transactionFlowMapper.selectWechatSettlementRecordList(param);
        enrichList(list);

        String fileName = defaultShopName(shop.getName()) + "结算记录";
        List<WriteHandler> handlers = Collections.singletonList(
                new SettlementRecordHeaderHandler(defaultShopName(shop.getName()), param, summary)
        );
        ExcelUtil.exportToWeb(response, list, fileName, "结算记录", SettlementRecordItemVo.class, handlers, 3);
    }

    private Result validateParam(SettlementRecordSearchParam param) {
        if (param.getShopId() == null) {
            return Result.error("商家ID不能为空");
        }
        if (param.getStartDate() != null && param.getEndDate() != null && param.getStartDate().isAfter(param.getEndDate())) {
            return Result.error("开始日期不能大于结束日期");
        }
        return Result.success("success");
    }

    private SettlementRecordSummaryVo getSummary(SettlementRecordSearchParam param) {
        SettlementRecordSummaryVo summary = transactionFlowMapper.selectWechatSettlementRecordSummary(param);
        if (summary == null) {
            summary = new SettlementRecordSummaryVo();
        }
        if (summary.getTotalCount() == null) {
            summary.setTotalCount(0L);
        }
        if (summary.getTotalAmount() == null) {
            summary.setTotalAmount(BigDecimal.ZERO);
        }
        summary.setTotalAmount(summary.getTotalAmount().setScale(2, RoundingMode.HALF_UP));
        return summary;
    }

    private void enrichList(List<SettlementRecordItemVo> list) {
        if (list == null) {
            return;
        }
        for (SettlementRecordItemVo item : list) {
            BigDecimal settlementAmount = defaultAmount(item.getSettlementAmount());
            item.setSettlementAmount(settlementAmount.setScale(2, RoundingMode.HALF_UP));

            BigDecimal totalRatePercent = defaultAmount(item.getPlatformRate()).add(convertHandlingRateToPercent());
            item.setRate(formatRatePercent(totalRatePercent));

            BigDecimal denominator = BigDecimal.ONE.subtract(
                    totalRatePercent.divide(PERCENT_DIVISOR, 6, RoundingMode.HALF_UP)
            );
            if (denominator.compareTo(BigDecimal.ZERO) <= 0) {
                item.setTurnover(BigDecimal.ZERO);
                continue;
            }
            item.setTurnover(settlementAmount.divide(denominator, 2, RoundingMode.HALF_UP));
        }
    }

    private BigDecimal defaultAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private String formatAmount(BigDecimal amount) {
        return defaultAmount(amount).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private BigDecimal convertHandlingRateToPercent() {
        return defaultAmount(handlingRate).multiply(ONE_HUNDRED);
    }

    private String formatRatePercent(BigDecimal totalRatePercent) {
        BigDecimal percent = defaultAmount(totalRatePercent).stripTrailingZeros();
        return percent.toPlainString() + "%";
    }

    private String defaultShopName(String shopName) {
        if (shopName == null || shopName.trim().isEmpty()) {
            return "商家";
        }
        return shopName.trim();
    }
}
