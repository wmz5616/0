package com.zemcho.ddql.controller.wechat.index.vo;

import lombok.Data;

/**
 * @title: RechargeOrderCountVo
 * @Description:
 * @Date: 2025/10/11 10:59
 */
@Data
public class RechargeOrderCountVo {
    //订单数量
    private Integer orderNum = 0;

    //支付总金额（充值金额） 单位为分
    private Integer amount = 0;

    //赠送金额 单位为分
    private Integer giveAmount = 0;

    //退款金额，单位为分
    private Integer refundAmount = 0;
}
