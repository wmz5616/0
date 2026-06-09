package com.zemcho.guzhe.util.tgy.dto;

import lombok.Data;

// 支付成功的回调结果
@Data
public class WxJsPayCallBackResponse {

    // 订单号(本地）
    private String lowOrderId;
    // 通莞金服订单号
    private String upOrderId;
    // 聚合支付的账号
    private String account;
    // 通莞金服商户编号
    private String merchantId;
    // 支付金额
    private String payMoney;
    // 支付方式（WX:微信 ,ZFB：支付宝）
    private String channelId;
    // 订单状态（0：成功，1：失败）
    private String state;
    // 订单描述
    private String orderDesc;
    // 支付时间
    private String payTime;
    // 消费者的openId
    private String openid;
    // 附加信息
    private String attach;
    // 渠道编码
    private String settlementChannel;
    // 结算结果 默认为null
    private String payoffType;
    // 签名
    private String sign;
}
