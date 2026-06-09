package com.zemcho.guzhe.controller.app.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppVersionUpdateParam {
    @NotNull(message = "版本id为空")
    private Integer versionId;
    /**
     * 下发状态：1-成功，2-失败
     */
    @NotNull(message = "下发状态为空")
    private Integer status;
}
