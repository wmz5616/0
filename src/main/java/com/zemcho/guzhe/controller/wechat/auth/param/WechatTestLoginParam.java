package com.zemcho.guzhe.controller.wechat.auth.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @title: WechatTestLoginParam
 * @Description:
 * @Date: 2024/7/8 10:00
 */
@Data
public class WechatTestLoginParam {
    @NotBlank(message = "参数异常")
    private String phone;

    @NotBlank(message = "参数异常!")
    private String pwd;
}
