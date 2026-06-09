package com.zemcho.ddql.service.login;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.dto.AuthAttrData;
import com.zemcho.ddql.controller.cas.param.AdminPhoneParam;
import com.zemcho.ddql.controller.login.param.LoginParam;
import com.zemcho.ddql.controller.login.param.PwdResetParam;

/**
 * @title: LoginService
 * @Description:
 * @Date: 2025/04/30 14:00
 */
public interface LoginService {

    /**
     * 登录
     *
     * @param loginParam
     * @return
     */
    Result login(LoginParam loginParam);

    /**
     * 注销登录
     *
     * @param token
     * @return
     */
    Result logout(String token);

    /**
     * 获取登录用户信息
     *
     * @param token
     * @param authAttrData
     * @return
     */
    Result getLoginInfo(String token, AuthAttrData authAttrData);

    Result resetPwd(PwdResetParam param, String token);

    /**
     * 忘记密码
     *
     * @param param
     * @return
     */
    Result forgetPassword(AdminPhoneParam param);
}
