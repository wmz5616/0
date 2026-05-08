package com.zemcho.guzhe.config.shiro;

import com.alibaba.fastjson.JSON;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.config.jwt.JWTToken;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.util.BeanUtil;
import com.zemcho.guzhe.util.Constant;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public class MiniFilter extends BasicHttpAuthenticationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        this.sendChallenge(request, response);
        return false;
    }

    // 检测header里面是否包含token字段
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("token");
        return authorization != null;
    }

    // 二次验证
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader("token");
        JWTToken token = new JWTToken(authorization);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(token);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    @SneakyThrows
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginAttempt(request, response)) {
            try {
                executeLogin(request, response);
                HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                // 小程序登录
                String token = httpServletRequest.getHeader("token");
                Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
                if (userId == null) {
                    sendMessage((HttpServletResponse) response, Result.error("token非法"));
                    throw new Exception();
                }
                RedisTemplate redisTemplate = BeanUtil.getBean("shiroRedisTemplate", RedisTemplate.class);
                redisTemplate.opsForValue().set(Constant.PREFIX_MINI_SHIRO_TOKEN + userId, token, 720,
                        TimeUnit.MINUTES);
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage((HttpServletResponse) response, Result.error("token验证失败"));
                throw new Exception();
            }

        } else {
            sendMessage((HttpServletResponse) response, Result.error("token为空"));
            throw new Exception();
        }
        return true;
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