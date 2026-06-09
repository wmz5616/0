package com.zemcho.guzhe.config.tgy_pay;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @title: MerchantConfig
 * @Description:
 * @Date: 2025/9/12 9:21
 */
@Configuration
@ConfigurationProperties(prefix = "merchant")
@Getter
@Setter
public class MerchantConfig {
    // 商户名
    private String name;

    // 主商户号
    private String parentMerchantNo;

    // 商户账号
    private String account;

    // 商户密钥
    private String key;

    // 进件商户登录账号
    private String loginAccount;

    // 进件商户号
    private String parentChannelMerchantNo;

    // 进件商户密钥
    private String parentChannelMerchantKey;

    // 分账-平台收费商户号
    private String ledgerNo;

    // 分账-平台收费商户名称
    private String ledgerName;

    // 小程序appid
    private String appId;

    // 获取微信支付接口
    private String wxJsPayURL;

    // 微信支付退款接口
    private String wxJsRefundURL;

    // 分账接口
    private String payDivideURL;

    // 文件上传接口
    private String uploadFileUrl;

    // 添加商户接口
    private String addMerchantUrl;

    // 查询商户接口
    private String queryMerchantUrl;

    // 商品订单订单支付完的回调接口地址
    private String productOrderCallBackUrl;
}
