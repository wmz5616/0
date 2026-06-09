package com.zemcho.guzhe.config.pool_config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @title: CustomAsyncPoolConfig
 * @Description:
 * @Date: 2025/3/3 17:30
 */
@Configuration
@ConfigurationProperties(prefix = "pool-config.custom-async")
@Getter
@Setter
public class CustomAsyncPoolConfig {
    //设置核心线程数
    private int corePoolSize;

    //设置最大线程数
    private int maxPoolSize;

    //设置空闲线程存活时间（秒）
    private int keepAliveSeconds;

    //设置队列容量
    private int queueCapacity;

    //设置线程池等待终止时间(秒)
    private int awaitTerminationSeconds;

    //设置线程名称前缀
    private String threadNamePrefix;
}
