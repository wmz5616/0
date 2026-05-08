package com.zemcho.guzhe.controller.wechat.shop.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BusinessDataVO {
    //营业额
    private BigDecimal revenue;
    //订单数
    private Integer orderCount;
    //待结算金额
    private BigDecimal pendingAmount;
}