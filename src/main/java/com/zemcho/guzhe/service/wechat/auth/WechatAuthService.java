package com.zemcho.guzhe.service.wechat.auth;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.wechat.auth.param.*;

public interface WechatAuthService {
    /**
     * 小程序登录
     *
     * @param param
     * @return
     */
    Result login(WechatAuthParam param);

    /**
     * 小程序本地登录-开发用
     *
     * @param param
     * @return
     */
    Result testLogin(WechatTestLoginParam param);

    /**
     * 微信一键绑定手机
     *
     * @param param
     * @param token
     * @return
     */
    Result bindWechatPhone(WechatAuthParam param, String token);

    /**
     * 验证码绑定手机
     *
     * @param param
     * @param token
     * @return
     */
    Result bindPhone(WechatBindPhoneParam param, String token);

    /**
     * 换绑手机号
     * @param param 包含手机号和验证码的参数
     * @param token 用户 token
     * @return Result
     */
    Result updateUserPhone(WechatBindPhoneParam param, String token);

    /**
     * 注销登录
     *
     * @param token
     * @return
     */
    Result logout(String token);

    /**
     * 获取当前登录人信息
     *
     * @param token
     * @return
     */
    Result getUserInfo(String token);

    /**
     * 更新用户信息
     *
     * @param param
     * @param token
     * @return
     */
    Result updateUser(WechatUpdateUserParam param, String token);

    /**
     * 用户实名认证
     *
     * @param param
     * @param token
     * @return
     */
    Result userCertification(CertificationParam param, String token);
}
