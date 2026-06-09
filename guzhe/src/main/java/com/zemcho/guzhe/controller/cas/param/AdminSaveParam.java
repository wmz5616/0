package com.zemcho.guzhe.controller.cas.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

/**
 * @title: AdminSaveParam
 * @Description:
 * @Date: 2025/5/9 17:20
 */
@Data
public class AdminSaveParam {
    private Integer id;

    @NotBlank(message = "手机号为空")
    private String account;

//    @NotBlank(message = "登录密码为空")
    @Pattern(regexp = "^[a-zA-Z\\d_]{6,20}$", message = "登录密码仅支持6~20位英文、数字和下划线_")
    private String password;

    @NotBlank(message = "用户名为空")
    private String name;

    @NotNull(message = "状态为空")
    private Integer status;

    private String remark = "";

    @NotNull(message = "角色为空")
    @NotEmpty(message = "角色为空!")
    private List<Integer> roleIds;
}
