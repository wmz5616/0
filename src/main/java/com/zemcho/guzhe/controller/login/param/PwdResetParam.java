package com.zemcho.guzhe.controller.login.param;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @title: PwdResetParam
 * @Description:
 * @Date: 2025/2/19 14:45
 */
@Data
public class PwdResetParam {
    @NotBlank(message = "旧密码为空")
    private String oldPassword;

    @NotBlank(message = "新密码为空")
    @Pattern(regexp = "^[a-zA-Z\\d_]{6,20}$", message = "登录密码仅支持6~20位英文、数字和下划线_")
    private String newPassword;

    @NotBlank(message = "确认密码为空")
    private String confirmPassword;
}
