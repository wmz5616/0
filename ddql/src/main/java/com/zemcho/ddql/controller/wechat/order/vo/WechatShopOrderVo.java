package com.zemcho.ddql.controller.wechat.order.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author HXH
 */
@Data
public class WechatShopOrderVo {
    private Integer id;
    private String orderNo;
    private Integer payAmount;
    private Integer deductCoin;
    private Integer userId;
    private String nickName;
    private String phone;
    private Integer status;
    private Integer totalAmount;
    private Integer deductAmount;

    private LocalDateTime refundTime;

    private LocalDateTime orderTime;
    private String refundReason;
}
