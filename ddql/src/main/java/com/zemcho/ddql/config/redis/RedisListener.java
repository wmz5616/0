package com.zemcho.ddql.config.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * @author Ryan
 * @title: RedisListener
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/7/30 0030 11:34
 */
public class RedisListener implements MessageListener {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onMessage(Message message, byte[] bytes) {
        String body = new String(message.getBody());
        logger.info("redis键过期事件 - {}", body);
    }
}
