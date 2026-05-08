package com.zemcho.guzhe.config.shiro;

import com.alibaba.fastjson.JSON;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.util.BeanUtil;
import com.zemcho.guzhe.util.redis.RedisUtil;
import lombok.SneakyThrows;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;

public class AppFilter extends BasicHttpAuthenticationFilter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        this.sendChallenge(request, response);
        return false;
    }

    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("accessToken");
        return authorization != null;
    }

    @SneakyThrows
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginAttempt(request, response)) {
            try {
                HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                boolean isAccess = checkAccessToken(httpServletRequest);
                if (!isAccess) {
                    sendMessage((HttpServletResponse) response, Result.error("accessToken已过期"));
                    throw new Exception();
                }
            } catch (Exception e) {
                sendMessage((HttpServletResponse) response, Result.error("accessToken验证失败"));
                throw new Exception();
            }

        } else {
            sendMessage((HttpServletResponse) response, Result.error("accessToken为空"));
            throw new Exception();
        }
        return true;
    }

    private boolean checkAccessToken(HttpServletRequest request) {
        String accessToken = request.getHeader("accessToken");
        RedisUtil redisUtil = BeanUtil.getBean("redisUtil", RedisUtil.class);
        if (redisUtil.hasKey(accessToken)) {
            return true;
        }

        return false;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control" +
                "-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    private void sendMessage(HttpServletResponse response, Result result) {
        response.setContentType("application/json;charset=UTF-8");
        try (OutputStream outputStream = response.getOutputStream()) {
            outputStream.write(JSON.toJSONBytes(result));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}