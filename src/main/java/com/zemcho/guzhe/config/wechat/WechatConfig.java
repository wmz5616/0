package com.zemcho.guzhe.config.wechat;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @title: WechatConfig
 * @Description:
 * @Date: 2024/7/5 14:37
 */
@Configuration
@ConfigurationProperties(prefix = "miniapp")
@Getter
@Setter
public class WechatConfig {
    private String appId;

    private String secret;

    // 小程序码环境
    private String codeQREnv;

    // 产品订单小程序码跳转页面
    private String productOrderQRPage;

    // 屏幕店位租赁订单小程序码跳转页面
    private String screenOrderQRPage;
}
