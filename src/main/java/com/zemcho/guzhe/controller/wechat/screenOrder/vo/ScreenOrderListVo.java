package com.zemcho.guzhe.controller.wechat.screenOrder.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 店位订单列表结果
 */
@Data
public class ScreenOrderListVo {
    /**
     * 订单数量
     */
    private Integer orderCount = 0;

    /**
     * 订单总金额，单位：分
     */
    private Integer totalAmount = 0;

    /**
     * 订单总金额文案，单位：元
     */
    private String totalAmountText = "0.00";

    /**
     * 订单列表
     */
    private List<ScreenOrderListItemVo> list = new ArrayList<>();
}
