package com.zemcho.guzhe.entity.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PayLog {
    private Integer id;

    private Integer orderType;

    private Integer orderId;

    private Integer userId;

    private String outTradeNo;

    private String wxTransactionNo;

    private String wxResult;

    private Integer totalFee;

    //状态：1支付成功，2支付失败，3已退款，4退款失败
    private Integer status;

    private String refundTradeNo;

    private Integer refundPrice;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refundTime;

    private String remark = "";

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}