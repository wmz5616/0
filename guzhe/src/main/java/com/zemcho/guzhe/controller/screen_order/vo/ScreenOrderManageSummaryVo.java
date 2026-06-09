package com.zemcho.guzhe.controller.screen_order.vo;

import lombok.Data;

/**
 * 后台店位订单汇总
 */
@Data
public class ScreenOrderManageSummaryVo {
    private Integer orderCount;

    private Integer totalAmount;
}
