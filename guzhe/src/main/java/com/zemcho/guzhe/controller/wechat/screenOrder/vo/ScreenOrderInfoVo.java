package com.zemcho.guzhe.controller.wechat.screenOrder.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 店位订单详情
 */
@Data
public class ScreenOrderInfoVo {
    private Long orderId;
    private String orderNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime orderTime;

    private Integer status;
    private String statusText;
    private Integer equipmentId;
    private String serialNumber;
    private Integer totalAmount; // 订单总金额，单位：分
    private String totalAmountText; // 订单总金额文案，单位：元
    private Integer businessCircleId; // 商超ID
    private String businessCircleName; // 商超名称
    private String businessCircleAddress; // 商超地址
    private Integer shopId; // 店位ID
    private String shopName;
    private String nickName; // 下单人
    private String phone; // 下单人手机号
    private String orderUserText; // 下单人
    private Integer displayType; // 展示内容类型
    private String displayTypeText; // 展示内容类型文案
    private String rentalMonths; // 租用月份
    private String remark; // 备注
    private String remarkLabel; // 备注文案
}
