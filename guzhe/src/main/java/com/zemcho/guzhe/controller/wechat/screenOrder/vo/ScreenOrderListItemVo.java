package com.zemcho.guzhe.controller.wechat.screenOrder.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 店位订单列表项
 */
@Data
public class ScreenOrderListItemVo {
    private Long orderId; // 订单ID
    private String orderNo; // 订单编号

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime orderTime; // 下单时间

    private Integer status; // 订单状态
    private String statusText;
    private Integer equipmentId;
    private String serialNumber; // 设备编号
    private Integer totalAmount; // 订单总金额，单位：分
    private String totalAmountText;
    private Integer businessCircleId;
    private String businessCircleName; // 商超名称
    private Integer shopId;
    private String shopName; // 店位名称
    private String rentalMonths; // 租用月份
    private String remark;
    private String remarkLabel;

    // 下单人
}
