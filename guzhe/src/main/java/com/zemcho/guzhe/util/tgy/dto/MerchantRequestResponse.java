package com.zemcho.guzhe.util.tgy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 商户信息进件或查询的响应
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantRequestResponse {

    private MerchantResponseData data;

    // 响应码 200:成功、500:内部错误、600:图片链接错误/签名校验不通过/其他错误
    private String code;

    // 响应描述 示例值 success
    private String desc;

}
