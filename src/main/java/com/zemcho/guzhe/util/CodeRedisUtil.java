package com.zemcho.guzhe.util;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Ryan
 */
public class CodeRedisUtil {
    private static final long CODE_TIMEOUT = 5L;

    private static RedisTemplate<String, Object> redisTemplate =
            BeanUtil.getBean("redisTemplate", RedisTemplate.class);

    private static String getKey(String number, Integer type) {
        return Constant.CODE_PREFIX + type + ":" + number;
    }

    private static String getBindKey(String number) {
        return Constant.BIND_PREFIX + number;
    }


    public static void addCode(String number, Integer type, String code) {
        String key = getKey(number, type);
        removeCode(number, type);
        redisTemplate.opsForValue().set(key, code, CODE_TIMEOUT, TimeUnit.MINUTES);
    }

    public static void addBindCode(String number, String code) {
        removeBindCode(number);
        String key = getBindKey(number);
        redisTemplate.opsForValue().set(key, code, CODE_TIMEOUT, TimeUnit.MINUTES);
    }

    /* delete */
    public static Boolean removeCode(String number, Integer type) {
        if (exist(number, type)) {
            String key = getKey(number, type);
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    /* delete */
    public static Boolean removeBindCode(String number) {
        if (existBind(number)) {
            String key = getBindKey(number);
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    /* get */
    public static String getCode(String number, Integer type) {

        String key = getKey(number, type);

        if (redisTemplate.getExpire(key) > 0) {
            return (String) redisTemplate.opsForValue().get(key);
        }

        return null;
    }

    /* get */
    public static String getBindCode(String number) {

        String key = getBindKey(number);

        if (redisTemplate.getExpire(key) > 0) {
            return (String) redisTemplate.opsForValue().get(key);
        }

        return null;
    }

    /* key是否还在有效期 */
    public static Boolean exist(String number, Integer type) {
        String key = getKey(number, type);
        if (redisTemplate.hasKey(key)) {
            if (redisTemplate.getExpire(key) > 0) {
                return true;
            }
        }
        return false;
    }

    /* key是否还在有效期 */
    public static Boolean existBind(String number) {
        String key = getBindKey(number);
        if (redisTemplate.hasKey(key)) {
            if (redisTemplate.getExpire(key) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成随机验证码
     *
     * @return 验证码
     */
    public static String generateCode(Integer length) {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(10);
            code.append(num);
        }
        return code.toString();
    }
}
