package com.zemcho.guzhe.config.jwt;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author Ryan
 * @title: JWTToken
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/6/23 0023 16:51
 */
public class JWTToken implements AuthenticationToken {
    // 密钥
    private String token;

    public JWTToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}