package com.zemcho.guzhe.controller.wechat.screen.vo;

import lombok.Data;

import java.util.List;

/**
 * 单个设备下单结果
 */
@Data
public class ScreenRentalRentItemVo {
    private Long orderId;

    private String orderNo;

    private Integer totalAmount;

    private Integer equipmentId;

    private Integer shopId;

    private Integer businessCircleId;

    private Integer displayType;

    private List<String> rentalMonths;
}
