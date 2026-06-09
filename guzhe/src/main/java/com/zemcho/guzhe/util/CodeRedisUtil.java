package com.zemcho.guzhe.util;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Ryan
 */
public class CodeRedisUtil {
    private static final long CODE_TIMEOUT = 5L;
    // 短信发送频率限制：1分钟内只能发送1次
    private static final long SMS_SEND_LIMIT_SECONDS = 60L;

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

    /* 短信频率限制相关 */
    private static String getSendLimitKey(String number) {
        return "sms_send_limit:" + number;
    }

    /**
     * 检查短信发送频率限制
     * @return true表示可以发送，false表示在冷却时间内
     */
    public static Boolean canSendSms(String number) {
        String key = getSendLimitKey(number);
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (ttl != null && ttl > 0) {
            return false;
        }
        return true;
    }

    /**
     * 记录短信发送时间，设置冷却时间
     */
    public static void markSmsSent(String number) {
        String key = getSendLimitKey(number);
        redisTemplate.opsForValue().set(key, "1", SMS_SEND_LIMIT_SECONDS, TimeUnit.SECONDS);
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
