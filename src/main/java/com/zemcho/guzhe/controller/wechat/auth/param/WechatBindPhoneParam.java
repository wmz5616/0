package com.zemcho.guzhe.controller.wechat.auth.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @title: WechatBindPhoneParam
 * @Description:
 * @Date: 2024/7/5 17:49
 */
@Data
public class WechatBindPhoneParam {
    @NotBlank(message = "code为空")
    private String code;

    @NotBlank(message = "phone为空")
    private String phone;
}
