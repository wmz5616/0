package com.zemcho.guzhe.config.shiro;

import com.alibaba.fastjson.JSON;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthAttrData;
import com.zemcho.guzhe.common.dto.AuthJwtData;
import com.zemcho.guzhe.config.jwt.JWTToken;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.config.other.OtherConfig;
import com.zemcho.guzhe.util.BeanUtil;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.redis.RedisUtil;
import lombok.SneakyThrows;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class JWTFilter extends BasicHttpAuthenticationFilter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        this.sendChallenge(request, response);
        return false;
    }

    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("token");
        return authorization != null;
    }

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
                String token = httpServletRequest.getHeader("token");
                AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
                if (authJwtData == null) {
                    sendMessage((HttpServletResponse) response, Result.error("token非法"));
                    throw new Exception();
                }
                String account = authJwtData.getAccount();

                RedisUtil redisUtil = BeanUtil.getBean("redisUtil", RedisUtil.class);
                Map<String, Object> permissionData =
                        redisUtil.hashEntries(Constant.ADMIN_PERMISSION_DATA_PREFIX + authJwtData.getAdminId());
                if (permissionData == null) {
                    sendMessage((HttpServletResponse) response, Result.error("无权限访问，请联系管理员"));
                    throw new Exception();
                }
                List<Integer> roleIds = (List<Integer>) permissionData.get("roleIds");
                List<String> authRules = (List<String>) permissionData.get("authRules");

                AuthAttrData authAttrData = new AuthAttrData();
                authAttrData.setRoleIds(roleIds);
                httpServletRequest.setAttribute(Constant.REQUEST_ATTR_DATA, authAttrData);

                boolean isAccess = checkApi(httpServletRequest, authJwtData.getAdminId(), authRules);
                if (!isAccess) {
                    sendMessage((HttpServletResponse) response, Result.error("无权限访问"));
                    throw new Exception();
                }
                RedisTemplate redisTemplate = BeanUtil.getBean("shiroRedisTemplate", RedisTemplate.class);
                redisTemplate.opsForValue().set(Constant.PREFIX_SHIRO_TOKEN + account, token, 120, TimeUnit.MINUTES);
            } catch (Exception e) {
                sendMessage((HttpServletResponse) response, Result.error("token验证失败"));
                throw new Exception();
            }

        } else {
            sendMessage((HttpServletResponse) response, Result.error("token为空"));
            throw new Exception();
        }
        return true;
    }

    private boolean checkApi(HttpServletRequest request, Integer userId, List<String> authRules) {
        List<String> commons = new ArrayList<>();
        commons.add("/cas/logout");
        commons.add("/cas/info");
        commons.add("/cas/pwd/reset");
        commons.add("/file/upload");

        List<String> reqCommons = new ArrayList<>();
        reqCommons.add("/common/*");
        reqCommons.add("/system/*");
        String currentApi = request.getServletPath();
        if (commons.contains(currentApi)) {
            return true;
        }
        for (String item : reqCommons) {
            int index = item.indexOf("*");
            String pre = item.substring(0, index);
            if (currentApi.startsWith(pre)) {
                return true;
            }
        }

        OtherConfig otherConfig = BeanUtil.getBean("otherConfig", OtherConfig.class);
        if (userId.equals(otherConfig.getSuperAdminId())) {
            return true;
        }

        List<String> apis = new ArrayList<>();
        for (String rule : authRules) {
            if (rule == null || "".equals(rule)) {
                continue;
            }
            List<String> itemList = Arrays.asList(rule.split(","));
            apis.addAll(itemList);
        }
        apis = apis.stream().distinct().collect(Collectors.toList());

        List<String> reqApis = apis.stream().filter(a -> {
            if (a.indexOf("*") >= 0) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());

        if (apis.contains(currentApi)) {
            return true;
        }
        for (String item : reqApis) {
            int index = item.indexOf("*");
            String pre = item.substring(0, index);
            if (currentApi.startsWith(pre)) {
                return true;
            }
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

    private boolean refreshToken(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("token");

        AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
        if (authJwtData == null) {
            return false;
        }
        String account = authJwtData.getAccount();

        RedisTemplate redisTemplate = BeanUtil.getBean("shiroRedisTemplate", RedisTemplate.class);

        if (redisTemplate.hasKey(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account)) {
            String currentTimeMillisRedis =
                    redisTemplate.opsForValue().get(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account).toString();
            if (JWTUtil.getClaim(token, Constant.CURRENT_TIME_MILLIS).equals(currentTimeMillisRedis)) {
                String currentTimeMillis = String.valueOf(System.currentTimeMillis());
                // 设置RefreshToken中的时间戳为当前最新时间戳，且刷新过期时间重新为30分钟过期
                redisTemplate.opsForValue().set(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account, currentTimeMillis,
                        1800, TimeUnit.SECONDS);
                // 刷新AccessToken，设置时间戳为当前最新时间戳
                token = JWTUtil.sign(authJwtData, currentTimeMillis);
                // 将新刷新的AccessToken再次进行Shiro的登录
                JWTToken jwtToken = new JWTToken(token);
                // 提交给UserRealm进行认证，如果错误他会抛出异常并被捕获，如果没有抛出异常则代表登入成功，返回true
                this.getSubject(request, response).login(jwtToken);
                // 最后将刷新的AccessToken存放在Response的Header中的Authorization字段返回
                HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
                httpServletResponse.setHeader("token", token);
                return true;
            }
        }
        return false;
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