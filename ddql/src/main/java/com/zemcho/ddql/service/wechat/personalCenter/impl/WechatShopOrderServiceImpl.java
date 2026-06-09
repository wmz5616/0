package com.zemcho.ddql.service.wechat.personalCenter.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.controller.wechat.personalCenter.param.WechatShopOrderInfoParam;
import com.zemcho.ddql.controller.wechat.personalCenter.vo.WechatShopOrderInfoVo;
import com.zemcho.ddql.controller.wechat.personalCenter.param.WechatShopOrderListParam;
import com.zemcho.ddql.controller.wechat.personalCenter.vo.WechatShopOrderListVo;
import com.zemcho.ddql.controller.wechat.personalCenter.vo.WechatShopOrderStatVo;
import com.zemcho.ddql.mapper.order.ShopOrderMapper;
import com.zemcho.ddql.service.wechat.personalCenter.WechatShopOrderService;
import com.zemcho.ddql.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class WechatShopOrderServiceImpl implements WechatShopOrderService {
    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Override
    public Result orderList(WechatShopOrderListParam param, String token) {
        // 先从小程序 token 中解析当前登录用户。
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        // 前端未传时间时，默认补齐最近 6 个月的查询区间。
        fillDefaultTime(param);
        if (param.getStartTime().isAfter(param.getEndTime())) {
            return Result.error("开始时间不能晚于结束时间");
        }
        // 日期入参转换成查询用的完整时间范围，覆盖整天数据。
        buildQueryTime(param);

        // 分页查询当前用户的门店订单列表。
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<WechatShopOrderListVo> list = shopOrderMapper.selectWechatOrderList(param, userId);
        PageInfo<WechatShopOrderListVo> pageInfo = new PageInfo<>(list);

        // 同步查询顶部统计信息，给前端渲染“总笔数/总金额”区域。
        WechatShopOrderStatVo statVo = shopOrderMapper.selectWechatOrderStat(param, userId);
        if (statVo == null) {
            statVo = new WechatShopOrderStatVo();
            statVo.setTotalNum(0);
            statVo.setTotalPayAmount(0);
        }

        return Result.success("获取成功", pageInfo, statVo);
    }

    @Override
    public Result orderInfo(WechatShopOrderInfoParam param, String token) {
        // 解析当前登录用户，详情接口同样只允许查询自己的订单。
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        // 主表查询时直接带上 userId，避免越权查看他人订单。
        WechatShopOrderInfoVo orderInfo = shopOrderMapper.selectWechatOrderInfo(param.getId(), userId);
        if (orderInfo == null) {
            return Result.error("订单不存在");
        }

        // 组装订单明细数据，供详情页展示。
        orderInfo.setDetailList(shopOrderMapper.selectDetailListByOrderId(param.getId()));

        return Result.success("获取成功", orderInfo);
    }

    private void fillDefaultTime(WechatShopOrderListParam param) {
        // 开始和结束时间都存在时，直接使用前端传值。
        if (param.getStartTime() != null && param.getEndTime() != null) {
            return;
        }

        // 其余情况统一回退到“当前时间往前 6 个月”。
        LocalDate endDate = LocalDate.now();
        param.setEndTime(endDate);
        param.setStartTime(endDate.minusMonths(6));
    }

    private void buildQueryTime(WechatShopOrderListParam param) {
        param.setQueryStartTime(param.getStartTime().atStartOfDay());
        param.setQueryEndTime(LocalDateTime.of(param.getEndTime(), LocalTime.MAX));
    }
}
