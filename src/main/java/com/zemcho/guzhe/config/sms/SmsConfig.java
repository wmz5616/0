package com.zemcho.guzhe.config.sms;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @title: SmsConfig
 * @Description:
 * @Date: 2023/11/8 11:38
 */
@Component
@Data
public class SmsConfig {
    @Value("${spring.sms.accessKeyId}")
    private String accessKeyIdTemp;

    @Value("${spring.sms.secret}")
    private String secretTemp;

    @Value("${spring.sms.signName}")
    private String signNameTemp;

    @Value("${spring.sms.codeTemplateId}")
    private String codeTemplateIdTemp;

    private static String accessKeyId;

    private static String secret;

    private static String signName;

    private static String codeTemplateId;

    @PostConstruct
    public void setAccessKeyId() {
        accessKeyId = this.accessKeyIdTemp;
    }

    public static String getAccessKeyId() {
        return accessKeyId;
    }

    @PostConstruct
    public void setSecret() {
        secret = this.secretTemp;
    }

    public static String getSecret() {
        return secret;
    }

    @PostConstruct
    public void setSignName() {
        signName = this.signNameTemp;
    }

    public static String getSignName() {
        return signName;
    }

    @PostConstruct
    public void setCodeTemplateId() {
        codeTemplateId = this.codeTemplateIdTemp;
    }

    public static String getCodeTemplateId() {
        return codeTemplateId;
    }
}
