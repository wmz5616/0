package com.zemcho.guzhe.controller.login;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthAttrData;
import com.zemcho.guzhe.controller.cas.param.AdminPhoneParam;
import com.zemcho.guzhe.controller.login.param.LoginParam;
import com.zemcho.guzhe.controller.login.param.PwdResetParam;
import com.zemcho.guzhe.service.login.LoginService;
import com.zemcho.guzhe.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @title: LoginController
 * @Description:
 * @Date: 2025/04/30 14:00
 */
@RestController
@RequestMapping("/cas")
public class LoginController {
    @Autowired
    private LoginService loginService;

    /**
     * 登录
     *
     * @param loginParam
     * @param result
     * @param request
     * @return
     */
    @RequestMapping("/login")
    public Result login(@Validated @RequestBody LoginParam loginParam, BindingResult result,
                        HttpServletRequest request) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        loginParam.setRequest(request);
        return loginService.login(loginParam);
    }

    /**
     * 更改密码
     *
     * @param token
     * @return
     */
    @RequestMapping("/pwd/reset")
    public Result resetPwd(@Validated @RequestBody PwdResetParam param, BindingResult result,
                           @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return loginService.resetPwd(param, token);
    }

    /**
     * 注销登录
     *
     * @param token
     * @return
     */
    @RequestMapping("/logout")
    public Result logout(@RequestHeader("token") String token) {
        return loginService.logout(token);
    }

    /**
     * 获取登录用户信息
     *
     * @param token
     * @param authAttrData
     * @return
     */
    @RequestMapping("/info")
    public Result getLoginInfo(@RequestHeader("token") String token,
                               @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        return loginService.getLoginInfo(token, authAttrData);
    }

    @Log(description = "忘记密码", module = "管理员管理")
    @RequestMapping("/forgetPassword")
    public Result forgetPassword(@Validated @RequestBody AdminPhoneParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return loginService.forgetPassword(param);
    }
}
