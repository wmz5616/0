package com.zemcho.guzhe.config.shiro;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.zemcho.guzhe.common.dto.AuthJwtData;
import com.zemcho.guzhe.config.jwt.JWTToken;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.util.BeanUtil;
import com.zemcho.guzhe.util.Constant;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;

/**
 * @author Ryan
 * @title: CustomRaalm
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/6/23 0023 10:28
 */
public class CustomRealm extends AuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        return simpleAuthorizationInfo;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) {
        String token = (String) authenticationToken.getCredentials();

        AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (authJwtData == null && userId == null) {
            throw new TokenExpiredException("token已过期", Instant.now());
        }

        RedisTemplate redisTemplate = BeanUtil.getBean("shiroRedisTemplate", RedisTemplate.class);
        if (authJwtData != null && redisTemplate.hasKey(Constant.PREFIX_SHIRO_TOKEN + authJwtData.getAccount())) {
            String tokenCache =
                    redisTemplate.opsForValue().get(Constant.PREFIX_SHIRO_TOKEN + authJwtData.getAccount()).toString();

            if (token.equals(tokenCache)) {
                return new SimpleAuthenticationInfo(token, token, getName());
            }
        }
        if (userId != null && redisTemplate.hasKey(Constant.PREFIX_MINI_SHIRO_TOKEN + userId)) {
            String tokenCache = redisTemplate.opsForValue().get(Constant.PREFIX_MINI_SHIRO_TOKEN + userId).toString();

            if (token.equals(tokenCache)) {
                return new SimpleAuthenticationInfo(token, token, getName());
            }
        }
        throw new TokenExpiredException("token已过期!", Instant.now());
    }
}