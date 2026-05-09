package com.zemcho.guzhe.controller.common.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @title: SmsCodeParam
 * @Description:
 * @Date: 2025/7/4 13:37
 */
@Data
public class SmsCodeParam {
    // 图形验证码的编号
    private String uuid;
    // 图形验证码
    private String captchaCode;

    @NotBlank(message = "号码为空")
    private String phone;

    // 验证码类型：1小程序登录绑定手机号   2 后台管理员忘记密码验证码  3 图形验证码
    @NotNull(message = "类型为空")
    private Integer type;
}
