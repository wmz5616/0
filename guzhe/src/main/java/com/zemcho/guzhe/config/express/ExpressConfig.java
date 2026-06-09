package com.zemcho.guzhe.config.express;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @title: ExpressConfig
 * @Description:
 * @Date: 2025/10/20 19:02
 */
@Configuration
@ConfigurationProperties(prefix = "express")
@Getter
@Setter
public class ExpressConfig {
    // 授权码
    private String key;

    // 订阅回调接口地址，默认仅支持http，如需兼容https请联系快递100技术人员处理
    private String subscribeCallback;
}
