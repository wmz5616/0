package com.zemcho.guzhe.aspect.log;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zemcho.guzhe.aspect.RequestWrapper;
import com.zemcho.guzhe.common.dto.AuthJwtData;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.entity.log.OperateLog;
import com.zemcho.guzhe.mapper.log.LogMapper;
import com.zemcho.guzhe.util.CommonUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ryan
 * @title: LogAspect
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/9/28 0028 17:24
 */
@Component
@Aspect
public class LogAspect {
    @Autowired
    LogMapper logMapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("@annotation(com.zemcho.guzhe.aspect.log.Log)")
    public void logPoinCut() {
    }

    //日志写入
    @AfterReturning(value = "logPoinCut()", returning = "rtv")
    public void afterLog(JoinPoint joinpoint, Object rtv) {
        logger.info("访问日志写入");
        OperateLog operateLog = new OperateLog();
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = CommonUtils.getUserIP(request);
        LocalDateTime now = LocalDateTime.now();

        operateLog.setIp(ip);
        operateLog.setOperateTime(now);
        operateLog.setApi(request.getServletPath());
        operateLog.setType(request.getMethod());

        RequestWrapper requestWrapper = new RequestWrapper(request);
        String jsonBody = requestWrapper.getBody();

        operateLog.setParam(jsonBody);

        String result = JSON.toJSONString(rtv);
//        String result = "";
        try {
            result = new ObjectMapper().writeValueAsString(rtv);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        operateLog.setResult(result);

        //用户信息
        String token = request.getHeader("token");
        if (token != null) {
            AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
            if (authJwtData != null) {
                operateLog.setAdminId(authJwtData.getAdminId());
                operateLog.setName(authJwtData.getName());
                operateLog.setAccount(authJwtData.getAccount());
            }
        }

        Signature signature = joinpoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        Log log = method.getAnnotation(Log.class);
        if (log != null) {
            operateLog.setDescription(log.description());
            operateLog.setModule(log.module());
        }

        logMapper.insertOperateLog(operateLog);
        logger.info("访问日志写入成功");
    }

    private Map<String, Object> getFieldsName(JoinPoint joinPoint) {
        String classType = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        // 参数值
        Object[] args = joinPoint.getArgs();
        Class<?>[] classes = new Class[args.length];
        for (int k = 0; k < args.length; k++) {
            if (args[k] instanceof MultipartFile ||
                    args[k] instanceof ServletRequest ||
                    args[k] instanceof ServletResponse ||
                    args[k] instanceof BindingResult) {
                continue;
            }
            if (!args[k].getClass().isPrimitive()) {

                Class s = args[k].getClass();

                classes[k] = s == null ? args[k].getClass() : s;
            }
        }
        ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
        // 获取指定的方法，第二个参数可以不传，但是为了防止有重载的现象，还是需要传入参数的类型
        Method method = null;
        try {
            method = Class.forName(classType).getMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            return new HashMap<>();
        } catch (ClassNotFoundException e) {
            return new HashMap<>();
        }
        // 参数名
        String[] parameterNames = pnd.getParameterNames(method);
        // 通过map封装参数和参数值
        HashMap<String, Object> paramMap = new HashMap();
        for (int i = 0; i < parameterNames.length; i++) {
            paramMap.put(parameterNames[i], args[i]);
        }
        return paramMap;
    }

    private static HashMap<String, Class> map = new HashMap<String, Class>() {
        {
            put("java.lang.Integer", int.class);
            put("java.lang.Double", double.class);
            put("java.lang.Float", float.class);
            put("java.lang.Long", long.class);
            put("java.lang.Short", short.class);
            put("java.lang.Boolean", boolean.class);
            put("java.lang.Char", char.class);
        }
    };
}
