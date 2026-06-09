package com.zemcho.guzhe.controller.wechat.auth.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @title: CertificationParam
 * @Description:
 * @Date: 2025/10/10 16:43
 */
@Data
public class CertificationParam {
    @NotBlank(message = "身份证号为空")
    private String cardNum;

    @NotBlank(message = "姓名为空")
    private String name;
}
