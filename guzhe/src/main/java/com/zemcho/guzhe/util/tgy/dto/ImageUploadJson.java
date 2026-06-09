package com.zemcho.guzhe.util.tgy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageUploadJson {

    // 图片base64编码，将data:image/png;base64前缀去掉后再提交
    private String fileStr;

    // 登录账号
    private String loginAccount;

    // 签名
    private String sign;

}
