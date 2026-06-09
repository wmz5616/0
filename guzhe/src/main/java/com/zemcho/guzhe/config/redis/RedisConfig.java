package com.zemcho.guzhe.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Ryan
 * @title: RedisConfig
 * @projectName master
 * @description: redis配置
 * @date 2020/7/22 0022 14:07
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Getter
@Setter
public class RedisConfig {
    /* IP */
    private String host;

    /* 密码 */
    private String password;

    /* 端口 */
    private int port;

    /* 过期时间（秒） */
    private int expire;

    /* 过期时间（毫秒） */
    private int expireMilliSecond = expire * 1000;

    /* 数据库Index */
    private int database;

    /* 最大空闲连接数 */
    private int maxIdle;

    /* 最小空闲连接数 */
    private int minIdle;

    /* 最大连接数 */
    private int maxTotal;

    /* 最大等待时长（毫秒） */
    private long maxWaitMills;

    @Bean(name = "jedisPoolConfig")
    public JedisPoolConfig jedisPoolConfig(){
        System.out.println("host = " + host);
        System.out.println("expire = " + expire);
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxWaitMillis(maxWaitMills);
        poolConfig.setJmxEnabled(false);

        return poolConfig;
    }

    @Bean("jedisPool")
    public JedisPool jedisPool(@Qualifier("jedisPoolConfig") JedisPoolConfig poolConfig){

        JedisPool jedisPool = new JedisPool(poolConfig, host, port, expire, password, database);
        return jedisPool;
    }

    @Bean(name = "jedisConnectionFactory")
    public JedisConnectionFactory jedisConnectionFactory(@Qualifier("jedisPoolConfig") JedisPoolConfig poolConfig){

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(host);
        jedisConnectionFactory.setPassword(password);
        jedisConnectionFactory.setDatabase(database);
        jedisConnectionFactory.setPort(port);
        jedisConnectionFactory.setTimeout(expire);
        jedisConnectionFactory.setPoolConfig(poolConfig);

        return jedisConnectionFactory;
    }
    @Bean(name = "shiroRedisTemplate")
    public RedisTemplate<String, Object> shiroRedisTemplate(@Qualifier("jedisConnectionFactory") JedisConnectionFactory factory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

    private RedisTemplate<String,Object> basicRedisTemplate(JedisConnectionFactory factory,boolean flag){

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("jedisConnectionFactory") JedisConnectionFactory factory) {

        return basicRedisTemplate(factory,true);
    }
}
