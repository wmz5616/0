package com.zemcho.ddql.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class OrderDivideLog {
    private Integer id;

    private Integer shopId;

    private String shopName;

    private Integer orderType;

    private Integer orderId;

    private String orderNo;

    private Integer amount;

    private String merchantNo;

    private String merchantName;

    private String divideNo;

    private Integer divideAmount;

    private Double handlingRate;

    private Integer handlingCharge;

    private String platformMerchantNo;

    private String platformMerchantName;

    private Double platformRate;

    private Integer platformCharge;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}