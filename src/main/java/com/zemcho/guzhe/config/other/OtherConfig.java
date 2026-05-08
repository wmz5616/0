package com.zemcho.guzhe.config.other;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "other")
@Getter
@Setter
public class OtherConfig {
    //超级管理员账号
    private Integer superAdminId;

    //是否更新菜单表数据
    private Boolean isUpdateRule;

    // app端签名密钥
    private String appSignSecret;

    // app端签名有效期
    private Integer appSignExpires;

    private Integer appAccessTokenRequireNumLimit;

    private Integer appAccessTokenRequireTimeLimit;

    private Integer appAccessTokenExpiresIn;
}
