package com.zemcho.guzhe.config.jwt;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zemcho.guzhe.common.dto.AppJwtData;
import com.zemcho.guzhe.common.dto.AuthJwtData;
import com.zemcho.guzhe.util.decode.Base64ConvertUtil;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.PropertiesUtil;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * @author Ryan
 * @title: JWTUtil
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/6/23 0023 16:43
 */
@Component
@PropertySource("classpath:param.properties")
public class JWTUtil {
    private static final long EXPIRE_TIME = 30 * 60 * 1000;

    private static String getEncryptJWTKey() {
        PropertiesUtil.readProperties("param.properties");
        return PropertiesUtil.getProperty("encryptJWTKey");
    }

    /**
     * 校验token是否正确
     *
     * @param token 密钥
     * @return 是否正确
     */
    public static boolean verify(String token) {
        try {
            AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
            if (authJwtData == null) {
                return false;
            }
            String secret = getClaim(token, authJwtData.getAccount()) + Base64ConvertUtil.decode(getEncryptJWTKey());
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     *
     * @return token中包含的用户名
     */
    public static String getClaim(String token, String claim) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(claim).asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 获取授权数据
     *
     * @param token
     * @return
     */
    public static AuthJwtData getAuthJwtData(String token) {
        try {
            String authData = getClaim(token, Constant.JWT_AUTH_DATA);
            if (authData == null) {
                return null;
            }
            return JSON.parseObject(authData, AuthJwtData.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param authJwtData       授权数据
     * @param currentTimeMillis 时间戳
     * @return 加密的token
     */
    public static String sign(AuthJwtData authJwtData, String currentTimeMillis) {
        try {
            String secret = authJwtData.getAccount() + Base64ConvertUtil.decode(getEncryptJWTKey());
            String authData = JSON.toJSONString(authJwtData);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带username信息
            return JWT.create()
                    .withClaim(Constant.JWT_AUTH_DATA, authData)
                    .withClaim(Constant.CURRENT_TIME_MILLIS, currentTimeMillis)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * miniSign
     *
     * @param userId
     * @param currentTimeMillis
     * @return
     */
    public static String miniSign(Integer userId, String currentTimeMillis) {
        try {
            String secret = "user:" + userId + Base64ConvertUtil.decode(getEncryptJWTKey());
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带username信息
            return JWT.create()
                    .withClaim(Constant.MINI_USER_ID, userId)
                    .withClaim(Constant.CURRENT_TIME_MILLIS, currentTimeMillis)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * getIntClaim
     *
     * @param token
     * @param claim
     * @return
     */
    public static Integer getIntClaim(String token, String claim) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(claim).asInt();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * appSign
     *
     * @param appJwtData
     * @param currentTimeMillis
     * @return
     */
    public static String appSign(AppJwtData appJwtData, String currentTimeMillis) {
        try {
            String secret = appJwtData.getSerialNumber() + Base64ConvertUtil.decode(getEncryptJWTKey());
            String authData = JSON.toJSONString(appJwtData);
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withClaim(Constant.APP_JWT_AUTH_DATA, authData)
                    .withClaim(Constant.CURRENT_TIME_MILLIS, currentTimeMillis)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * getAppAuthJwtData
     *
     * @param token
     * @return
     */
    public static AppJwtData getAppAuthJwtData(String token) {
        try {
            String authData = getClaim(token, Constant.APP_JWT_AUTH_DATA);
            if (authData == null) {
                return null;
            }
            return JSON.parseObject(authData, AppJwtData.class);
        } catch (Exception e) {
            return null;
        }
    }
}