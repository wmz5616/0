package com.zemcho.ddql.config.wechat;

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

    // 步数达标金币奖励通知模板id
    private String stepGoldTemplateId;

    // 商品退款成功通知模板id
    private String productRefundTemplateId;

    // 商品退款审核驳回通知模板id
    private String productRejectionTemplateId;
}
