package com.zemcho.guzhe.service.wechat.screenOrder.impl;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderInfoParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderListParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.vo.ScreenOrderInfoVo;
import com.zemcho.guzhe.controller.wechat.screenOrder.vo.ScreenOrderListItemVo;
import com.zemcho.guzhe.controller.wechat.screenOrder.vo.ScreenOrderListVo;
import com.zemcho.guzhe.controller.wechat.screenOrder.vo.ScreenOrderSummaryVo;
import com.zemcho.guzhe.entity.cas.CasUser;
import com.zemcho.guzhe.mapper.cas.CasUserMapper;
import com.zemcho.guzhe.mapper.screen.ScreenRentalOrderMapper;
import com.zemcho.guzhe.service.wechat.screenOrder.ScreenOrderService;
import com.zemcho.guzhe.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

/**
 * 店位订单
 */
@Service
public class IScreenOrderService implements ScreenOrderService {
    @Autowired
    private ScreenRentalOrderMapper screenRentalOrderMapper;

    @Autowired
    private CasUserMapper casUserMapper;

    @Override
    public Result lists(ScreenOrderListParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }
        if (userInfo.getAdminId() == null || userInfo.getAdminId() <= 0) {
            return Result.error("当前用户不是商家管理员");
        }

        // 原型默认本月，因此未传时间时按“本月第一天 ~ 本月最后一天”查询
        LocalDate startDate = param.getStartDate();
        LocalDate endDate = param.getEndDate();
        LocalDate now = LocalDate.now();
        if (startDate == null) {
            startDate = now.withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = now.withDayOfMonth(now.lengthOfMonth());
        }
        if (startDate.isAfter(endDate)) {
            return Result.error("开始时间不能大于结束时间");
        }

        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);

        ScreenOrderSummaryVo summary =
                screenRentalOrderMapper.selectWechatOrderSummary(param, userInfo.getAdminId(), startTime, endTime);
        if (summary == null) {
            summary = new ScreenOrderSummaryVo();
        }
        if (summary.getOrderCount() == null) {
            summary.setOrderCount(0);
        }
        if (summary.getTotalAmount() == null) {
            summary.setTotalAmount(0);
        }

        List<ScreenOrderListItemVo> list =
                screenRentalOrderMapper.selectWechatOrderLists(param, userInfo.getAdminId(), startTime, endTime);
        if (list == null) {
            list = Collections.emptyList();
        }

        for (ScreenOrderListItemVo item : list) {
            item.setStatusText(buildStatusText(item.getStatus()));
            item.setTotalAmountText(formatAmount(item.getTotalAmount()));
            if (item.getStatus() != null && item.getStatus() == 4) {
                item.setRemarkLabel("驳回原因");
            } else if (item.getStatus() != null && item.getStatus() == 5) {
                item.setRemarkLabel("撤销原因");
            }
        }

        ScreenOrderListVo result = new ScreenOrderListVo();
        result.setOrderCount(summary.getOrderCount());
        result.setTotalAmount(summary.getTotalAmount());
        result.setTotalAmountText(formatAmount(summary.getTotalAmount()));
        result.setList(list);
        return Result.success("获取成功", result);
    }

    @Override
    public Result info(ScreenOrderInfoParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }
        if (userInfo.getAdminId() == null || userInfo.getAdminId() <= 0) {
            return Result.error("当前用户不是商家管理员");
        }

        ScreenOrderInfoVo info = screenRentalOrderMapper.selectWechatOrderInfo(param.getOrderId(), userInfo.getAdminId());
        if (info == null) {
            return Result.error("订单不存在");
        }

        info.setStatusText(buildStatusText(info.getStatus()));
        info.setTotalAmountText(formatAmount(info.getTotalAmount()));
        info.setDisplayTypeText(buildDisplayTypeText(info.getDisplayType()));
        info.setOrderUserText(buildOrderUserText(info.getNickName(), info.getPhone()));
        if (info.getStatus() != null && info.getStatus() == 4) {
            info.setRemarkLabel("驳回原因");
        } else if (info.getStatus() != null && info.getStatus() == 5) {
            info.setRemarkLabel("撤销原因");
        }
        return Result.success("获取成功", info);
    }

    /**
     * 订单状态文案按原型输出，5 对外统一显示为“已取消”。
     */
    private String buildStatusText(Integer status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case 0 -> "待确认";
            case 1 -> "待生效";
            case 2 -> "生效中";
            case 3 -> "已完成";
            case 4 -> "已驳回";
            case 5 -> "已取消";
            default -> "";
        };
    }

    /**
     * 分转元，统一保留 2 位小数。
     */
    private String formatAmount(Integer amount) {
        if (amount == null) {
            amount = 0;
        }
        return BigDecimal.valueOf(amount)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .toPlainString();
    }

    /**
     * 展示内容类型文案。
     */
    private String buildDisplayTypeText(Integer displayType) {
        if (displayType == null) {
            return "";
        }
        return switch (displayType) {
            case 1 -> "商品";
            case 2 -> "海报";
            default -> "";
        };
    }

    /**
     * 原型里“下单人”一行展示昵称和手机号，这里直接拼成前端可直接使用的文案。
     */
    private String buildOrderUserText(String nickName, String phone) {
        String safeNickName = nickName == null ? "" : nickName.trim();
        String safePhone = phone == null ? "" : phone.trim();
        if (safeNickName.isEmpty()) {
            return safePhone;
        }
        if (safePhone.isEmpty()) {
            return safeNickName;
        }
        return safeNickName + " " + safePhone;
    }
}
