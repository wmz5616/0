package com.zemcho.guzhe.controller.cas.param;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminPhoneParam {

    // 手机号
    @NotBlank
    private String phone;

    // 验证码
    @NotBlank
    private String code;

    // 新密码
    @NotBlank
    private String newPassword;

    // 确认新密码
    @NotBlank
    private String confirmPassword;

}
