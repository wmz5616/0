package com.zemcho.guzhe.controller.wechat.auth.param;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @title: WechatBindPhoneParam
 * @Description:
 * @Date: 2024/7/5 17:49
 */
@Data
public class WechatBindPhoneParam {
    @NotBlank(message = "新手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "验证码不能为空")
    private String code;
}
