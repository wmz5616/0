package com.zemcho.guzhe.controller.screen_order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台店位订单列表项
 */
@Data
public class ScreenOrderManageItemVo {
    private Long orderId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime orderTime;

    private String serialNumber;

    private String businessCircleName;

    private String merchantName;

    private Integer totalAmount;

    private String totalAmountText;

    private String rentalMonths;

    private String orderUserText;

    private String orderNo;

    private Integer status;

    private String statusText;

    private String remark;

    private String remarkLabel;
}
