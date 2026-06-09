package com.zemcho.guzhe.controller.merchant.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @title: UploadMerchantImageParam
 * @Description:
 * @Date: 2025/9/11 19:29
 */
@Data
public class UploadMerchantImageParam {
    @NotBlank(message = "请上传图片")
    private String fileStr;
}
