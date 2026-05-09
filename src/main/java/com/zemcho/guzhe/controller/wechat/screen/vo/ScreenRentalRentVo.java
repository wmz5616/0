package com.zemcho.guzhe.controller.wechat.screen.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 屏幕店位租用结果
 */
@Data
public class ScreenRentalRentVo {
    /**
     * 本次提交生成的订单数量
     */
    private Integer orderCount = 0;

    /**
     * 本次提交总金额
     */
    private Integer totalAmount = 0;

    /**
     * 拆单后的订单结果
     */
    private List<ScreenRentalRentItemVo> orderList = new ArrayList<>();
}
