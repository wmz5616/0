package com.zemcho.guzhe.config.shiro;

import com.zemcho.guzhe.common.dto.AuthJwtData;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.util.BeanUtil;
import com.zemcho.guzhe.util.Constant;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CustomCache<K, V> implements Cache<K, V> {
    /**
     * 过期时间-5分钟
     */
    private static final Integer EXPIRE_TIME = 5 * 60;

    /**
     * @description: ZEMCHO
     * @author Ryan
     * @date 2020/6/29 0029
     * @time 11:56
     */
    private String getKey(Object key) {
        AuthJwtData authJwtData = JWTUtil.getAuthJwtData(key.toString());
        if (authJwtData == null) {
            return "";
        }
        String account = authJwtData.getAccount();

        return Constant.PREFIX_SHIRO_TOKEN + account;
    }

    /**
     * 获取缓存
     */
    @Override
    public Object get(Object key) throws CacheException {
        RedisTemplate redisTemplate = BeanUtil.getBean("shiroRedisTemplate", RedisTemplate.class);
        if (!redisTemplate.hasKey(this.getKey(key))) {
            return null;
        }
        return redisTemplate.opsForValue().get(this.getKey(key));
    }

    /**
     * 保存缓存
     */
    @Override
    public Object put(Object key, Object value) throws CacheException {
        RedisTemplate redisTemplate = BeanUtil.getBean("shiroRedisTemplate", RedisTemplate.class);
        // 设置Redis的Shiro缓存
        redisTemplate.opsForValue().set(this.getKey(key), value, EXPIRE_TIME, TimeUnit.SECONDS);
        return redisTemplate.opsForValue().get(this.getKey(key));
    }

    /**
     * 移除缓存
     */
    @Override
    public Object remove(Object key) throws CacheException {
        RedisTemplate redisTemplate = BeanUtil.getBean("shiroRedisTemplate", RedisTemplate.class);
        if (!redisTemplate.hasKey(this.getKey(key))) {
            return null;
        }
        redisTemplate.delete(this.getKey(key));
        return null;
    }

    /**
     * 清空所有缓存
     */
    @Override
    public void clear() throws CacheException {
        RedisTemplate redisTemplate = BeanUtil.getBean("shiroRedisTemplate", RedisTemplate.class);
        redisTemplate.delete(keys());
    }

    /**
     * 缓存的个数
     */
    @Override
    public int size() {
        return keys().size();
    }

    /**
     * 获取所有的key
     */
    @Override
    public Set keys() {
        RedisTemplate redisTemplate = BeanUtil.getBean("shiroRedisTemplate", RedisTemplate.class);
        return redisTemplate.keys("*");
    }

    /**
     * 获取所有的value
     */
    @Override
    public Collection values() {
        RedisTemplate redisTemplate = BeanUtil.getBean("shiroRedisTemplate", RedisTemplate.class);
        Set keys = this.keys();
        List<Object> values = new ArrayList<Object>();
        for (Object key : keys) {
            values.add(redisTemplate.opsForValue().get(this.getKey(key)));
        }
        return values;
    }
}