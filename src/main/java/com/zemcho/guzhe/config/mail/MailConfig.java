package com.zemcho.guzhe.config.mail;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
@Getter
@Setter
@Slf4j
public class MailConfig {

    private String host;

    private String username;

    private String password;

    private String protocol;

    private String defaultEncoding;

    private Properties properties;

    @Bean(name = "javaMailSender")
    public JavaMailSender javaMailSender(){

        System.out.println(properties);

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        javaMailSender.setProtocol(protocol);
        javaMailSender.setDefaultEncoding(defaultEncoding);
        javaMailSender.setJavaMailProperties(properties);


        return javaMailSender;
    }
}