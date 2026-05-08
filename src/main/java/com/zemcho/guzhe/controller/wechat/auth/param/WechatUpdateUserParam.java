package com.zemcho.guzhe.controller.wechat.auth.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @title: WechatUpdateUserParam
 * @Description:
 * @Date: 2024/7/8 10:00
 */
@Data
public class WechatUpdateUserParam {
    @NotBlank(message = "昵称为空")
    private String nickname;

    @NotBlank(message = "头像为空")
    private String avatar;
}
