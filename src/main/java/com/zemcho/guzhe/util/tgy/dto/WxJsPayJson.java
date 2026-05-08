package com.zemcho.guzhe.util.tgy.dto;

import lombok.Data;

// 创建微信支付时的传参
@Data
public class WxJsPayJson {
    // 聚合支付账号
    private String account;
    // 支付金额 单位 元
//    private String payMoney;
    private Double payMoney;
    // 下游订单号
    private String lowOrderId;
    // 值为‘1’，下游订单号唯一，不传此字段则下游订单号可重复请求，但返回upOrderId不是同一笔订单
    private String isUniqueLowOrderId;
    // 商品描述
    private String body;
    // 签名
    private String sign;
    // 回调地址 必须是外网可以访问，接收支付结果通知
    private String notifyUrl;
    // 支付完成前端返回地址（仅限公众号支付）
    private String returnUrl;
    // appid
    private String appId;
    // openId 支付用户的openId
    private String openId;
    // 清算结果回调地址（仅限易宝分账使用）
    private String csNotifyUrl;
    // 附加信息
    private String attach;
    // 门店号
    private String storeId;
    // 值为1，表示小程序支付；值为0，表示公众号支付
    private String isMinipg;
    // 易宝渠道使用可选值：DELAY_SETTLE
    private String fundProcessType;
    // 乐刷和微信渠道使用，Y表示分账订单，其他表示订单不分账
    private String profitSharing;
    // 下游收银员编号
    private String lowCashier;
    // 医疗场景值，医疗机构必传，其他非必传
    private String secenType;
    // 限制支付方式
    //1：限制信用卡
    //2：限制信用卡和花呗
    private String payLimit;
    // “Y”:易宝托管下单，“F”:富友插件下单
    private String tutelage;
    // 易宝营销补贴
    //示例：[{"type":"CUSTOM_ALLOWANCE"}]
    private String promotionInfo;

}
