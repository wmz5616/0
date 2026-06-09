package com.zemcho.guzhe.util.tgy.dto;

import lombok.Data;

// 创建微信支付时的结果
@Data
public class WxJsPayResponse {
    // 通莞订单号
    private String upOrderId;
    // json格式的字符串，作用于原生态js支付是的参数  返回给前端
    private String pay_info;
    // 签名 需要验签
    private String sign;
    // 信息描述
    private String message;
    // 微信禁用pay_url,返参为null
    private String pay_url;
    // 状态
    private Integer status;
}
