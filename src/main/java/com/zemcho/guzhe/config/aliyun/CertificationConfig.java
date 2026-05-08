package com.zemcho.guzhe.config.aliyun;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @title: CertificationConfig
 * @Description:
 * @Date: 2025/10/10 15:51
 */
@Configuration
@ConfigurationProperties(prefix = "aliyun.certification")
@Getter
@Setter
public class CertificationConfig {
    private String domain;

    private String xatDomain;

    private String appKey;

    private String appSecret;

    private String appCode;
}
