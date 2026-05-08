package com.zemcho.guzhe.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.util.AuthCheckUtil;
import com.zemcho.guzhe.util.Constant;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Order(1)
public class AuthAspect {
    @Getter
    @Setter
    @Value("${spring.profiles.active}")
    private String active;

    @Getter
    @Setter
    @Value("${auth-status}")
    private Boolean authStatus;

    @Resource(name = "shiroRedisTemplate")
    RedisTemplate redisTemplate;

    @Pointcut("execution(public * com.zemcho.guzhe.controller..*.*(..))")
    public void authPoinCut() {
    }

    //授权验证
    @Around("authPoinCut()")
    public Object doBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!active.equals("prod") || !authStatus) {
            return joinPoint.proceed();
        }
        try {
            String data = (String) redisTemplate.opsForValue().get(Constant.AUTH_CHECK);
            if (data == null || "".equals(data)) {
                data = AuthCheckUtil.checkAuth();
                if (data == null || "".equals(data)) {
                    return new Result(10006, "系统无权限，请联系软件提供商");
                }
                String dataStr = AuthCheckUtil.DESDecrypt(data);
                JSONObject dataObj = JSON.parseObject(dataStr);
                if (dataObj.getInteger("code") != 0) {
                    return new Result(10006, "系统无权限，请联系软件提供商");
                } else {
                    redisTemplate.opsForValue().set(Constant.AUTH_CHECK, data, 1, TimeUnit.DAYS);
                }
            } else {
                String dataStr = AuthCheckUtil.DESDecrypt(data);
                JSONObject dataObj = JSON.parseObject(dataStr);
                String now = String.valueOf(System.currentTimeMillis() / 1000);
                Integer nowTime = Integer.valueOf(now);
                Integer timeCheck = nowTime - dataObj.getInteger("timestamp");
                if (dataObj.getInteger("code") != 0 || timeCheck > (24 * 60 * 60)) {
                    data = AuthCheckUtil.checkAuth();
                    if (data == null || "".equals(data)) {
                        return new Result(10006, "系统无权限，请联系软件提供商");
                    }
                    dataStr = AuthCheckUtil.DESDecrypt(data);
                    dataObj = JSON.parseObject(dataStr);
                    if (dataObj.getInteger("code") != 0) {
                        return new Result(10006, "系统无权限，请联系软件提供商");
                    } else {
                        redisTemplate.opsForValue().set(Constant.AUTH_CHECK, data, 1, TimeUnit.DAYS);
                    }
                }
            }
        } catch (Exception e) {
            return new Result(10004, "授权错误，请联系管理员！");
        }
        return joinPoint.proceed();
    }
}