package com.zemcho.ddql.controller.wechat.shop.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BusinessDataVO {
    /**
     * 营业额
     */
    private BigDecimal revenue;

    /**
     * 订单数量
     */
    private Integer orderCount;

    /**
     * 待处理金额
     */
    private BigDecimal pendingAmount;

}