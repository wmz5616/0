package com.zemcho.guzhe.controller.login.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Ryan
 * @title: LoginDTO
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/7/29 0029 11:18
 */
@Data
public class LoginParam {
    @NotBlank(message = "账号为空")
    private String account;

    @NotBlank(message = "密码为空")
    private String password;

    private HttpServletRequest request;
}
