package com.zemcho.guzhe.controller.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 图形验证码的返回参数
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaCodeVo {
    // 图形码的标识id
    private String uuid;
    // base64编码的图片
    private String imageBase64;
}
