package com.zemcho.guzhe.controller.app.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class AppAuthParam {
    @NotBlank(message = "序列号为空")
    private String serialNumber;

    @NotBlank(message = "签名为空")
    private String sign;

    @NotNull(message = "签名生成时间戳为空")
    private Long singTime;
}
