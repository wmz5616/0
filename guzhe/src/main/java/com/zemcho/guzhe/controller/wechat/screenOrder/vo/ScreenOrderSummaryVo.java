package com.zemcho.guzhe.controller.wechat.screenOrder.vo;

import lombok.Data;

/**
 * 店位订单汇总
 */
@Data
public class ScreenOrderSummaryVo {
    private Integer orderCount;
    private Integer totalAmount;
}
