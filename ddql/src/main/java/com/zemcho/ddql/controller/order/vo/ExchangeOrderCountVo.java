package com.zemcho.ddql.controller.order.vo;

import lombok.Data;

/**
 * @title: ExchangeOrderCountVo
 * @Description:
 * @Date: 2025/10/14 15:02
 */
@Data
public class ExchangeOrderCountVo {
    // 订单数量
    private Integer orderNum;

    // 订单金额
    private Integer totalAmount;

    // 退款金额
    private Integer totalRefundAmount;

    // 兑换数量
    private Integer totalNum;
}
