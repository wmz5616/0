package com.zemcho.guzhe.controller.screen_order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 后台店位订单详情
 */
@Data
public class ScreenOrderManageInfoVo {
    private Long orderId;

    private String orderNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime orderTime;

    private Integer status;

    private String statusText;

    private Integer equipmentId;

    private String serialNumber;

    private Integer totalAmount;

    private String totalAmountText;

    private Integer businessCircleId;

    private String businessCircleName;

    private String businessCircleAddress;

    private Integer shopId;

    private String shopName;

    private String merchantName;

    private String nickName;

    private String phone;

    private String orderUserText;

    private Integer displayType;

    private String displayTypeText;

    private String rentalMonths;

    private String remark;

    private String remarkLabel;

    private Boolean canAudit = false;

    private Boolean canCancel = false;

    private List<ScreenOrderManageLogVo> operationRecords = new ArrayList<>();
}
