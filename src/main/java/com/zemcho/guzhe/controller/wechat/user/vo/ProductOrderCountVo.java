package com.zemcho.guzhe.controller.wechat.user.vo;

import lombok.Data;

/**
 * @title: ProductOrderCountVo
 * @Description:
 * @Date: 2026/4/28 14:10
 */
@Data
public class ProductOrderCountVo {
    // 订单数量
    private Integer orderNum;

    // 订单金额
    private Integer totalAmount;

    // 退款金额
    private Integer totalRefundAmount;

    // 数量
    private Integer totalNum;
}
