package com.zemcho.guzhe.controller.wechat.auth;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.wechat.auth.param.*;
import com.zemcho.guzhe.service.wechat.auth.WechatAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @title: WechatAuthController
 * @Description:
 * @Date: 2024/7/5 15:33
 */
@RestController
@RequestMapping("/wechat/cas")
public class WechatAuthController {
    @Autowired
    WechatAuthService service;

    /**
     * 小程序登录
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/login")
    public Result login(@Validated @RequestBody WechatAuthParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.login(param);
    }

    /**
     * 小程序本地登录-开发用
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/test/login")
    public Result testLogin(@Validated @RequestBody WechatTestLoginParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.testLogin(param);
    }

    /**
     * 微信一键绑定手机
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/bindWechatPhone")
    public Result bindWechatPhone(@Validated @RequestBody WechatAuthParam param, BindingResult result,
                                  @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.bindWechatPhone(param, token);
    }

    /**
     * 验证码绑定手机
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/bindPhone")
    public Result bindPhone(@Validated @RequestBody WechatBindPhoneParam param, BindingResult result,
                            @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.bindPhone(param, token);
    }

    /**
     * 注销登录
     *
     * @param token
     * @return
     */
    @RequestMapping("/logout")
    public Result logout(@RequestHeader("token") String token) {
        return service.logout(token);
    }

    /**
     * 获取当前登录人信息
     *
     * @param token
     * @return
     */
    @RequestMapping("/user/info")
    public Result userInfo(@RequestHeader("token") String token) {
        return service.getUserInfo(token);
    }

    /**
     * 更新用户信息
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/user/update")
    public Result updateUser(@Validated @RequestBody WechatUpdateUserParam param, BindingResult result,
                             @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.updateUser(param, token);
    }

    /**
     * 用户实名认证
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/user/certification")
    public Result userCertification(@Validated @RequestBody CertificationParam param, BindingResult result,
                                    @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.userCertification(param, token);
    }
}
