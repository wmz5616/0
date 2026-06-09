package com.zemcho.ddql.controller.wechat.personalCenter.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WechatShopOrderListVo {
    private Integer id;
    private String shopName;
    private Integer status;
    private String refundReason;
    private Integer payAmount;
    private Integer deductCoin;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;
}
