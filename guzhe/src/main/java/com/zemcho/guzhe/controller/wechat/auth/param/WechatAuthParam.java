package com.zemcho.guzhe.controller.wechat.auth.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @title: WechatAuthParam
 * @Description:
 * @Date: 2024/7/5 15:37
 */
@Data
public class WechatAuthParam {
    @NotBlank(message = "code为空")
    private String code;

    @NotBlank(message = "encryptedData为空")
    private String encryptedData;

    @NotBlank(message = "iv为空")
    private String iv;
}
