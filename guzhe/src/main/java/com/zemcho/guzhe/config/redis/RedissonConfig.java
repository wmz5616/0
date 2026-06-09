package com.zemcho.guzhe.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @title: RedissonConfig
 * @Description:
 * @Date: 2023/7/31 11:52
 */
@Configuration
public class RedissonConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.password}")
    private String password;

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient() {
        Config config = new Config();
        if (password == null || password.isEmpty()) {
            config.useSingleServer().setAddress("redis://" + host + ":" + port); // 更多.set
        } else {
            config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword(password); // 更多.set
        }
        return Redisson.create(config);
    }
}
