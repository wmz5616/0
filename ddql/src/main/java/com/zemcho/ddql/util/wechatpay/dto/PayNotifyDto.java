package com.zemcho.ddql.util.wechatpay.dto;

import lombok.Data;

/**
 * @title: PayNotifyDto
 * @Description: 微信支付 V3 支付成功回调解密后的数据结构
 * @Date: 2025/10/11 14:01
 */
@Data
public class PayNotifyDto {
    /**
     * 通知的唯一ID
     */
    private String id;

    /**
     * 通知创建时间，如 "2018-06-08T10:34:56+08:00"
     */
    private String createTime;

    /**
     * 通知的类型，支付成功为 TRANSACTION.SUCCESS
     */
    private String eventType;

    /**
     * 通知的摘要
     */
    private String summary;

    /**
     * 解密后的支付信息（核心数据）
     */
    private Resource resource;

    /**
     * Resource 内部类：包含实际的支付数据
     */
    @Data
    public static class Resource {
        /**
         * 商户号
         */
        private String mchid;

        /**
         * 公众号/小程序 appId
         */
        private String appid;

        /**
         * 商户订单号
         */
        private String outTradeNo;

        /**
         * 微信支付订单号
         */
        private String transactionId;

        /**
         * 交易类型，如 JSAPI、NATIVE、APP 等
         */
        private String tradeType;

        /**
         * 交易状态：SUCCESS、REFUND、NOTPAY、CLOSED 等
         */
        private String tradeState;

        /**
         * 交易状态描述
         */
        private String tradeStateDesc;

        /**
         * 支付金额信息
         */
        private Amount amount;

        /**
         * 支付者信息
         */
        private Payer payer;

        /**
         * 附加数据（原请求中传入的 attach）
         */
        private String attach;

        /**
         * 支付完成时间
         */
        private String successTime;

        /**
         * 银行类型，如 CMC
         */
        private String bankType;
    }

    /**
     * 金额详情
     */
    @Data
    public static class Amount {
        /**
         * 订单总金额，单位为分
         */
        private Integer total;

        /**
         * 用户支付金额，单位为分
         */
        private Integer payerTotal;

        /**
         * 货币类型，如 CNY
         */
        private String currency;

        /**
         * 用户支付币种，如 CNY
         */
        private String payerCurrency;
    }

    /**
     * 支付者信息
     */
    @Data
    public static class Payer {
        /**
         * 用户在商户 appid 下的唯一标识
         */
        private String openid;
    }

    /**
     * 是否支付成功
     */
    public boolean isSuccess() {
        return "SUCCESS".equals(this.resource.tradeState);
    }

    /**
     * 获取用户 openid
     */
    public String getPayerOpenid() {
        if (resource.payer != null) {
            return resource.payer.openid;
        }
        return null;
    }

    /**
     * 获取outTradeNo
     */
    public String getOutTradeNo() {
        return resource.outTradeNo;
    }

    /**
     * 获取transactionId
     *
     * @return
     */
    public String getTransactionId() {
        return resource.transactionId;
    }
}
