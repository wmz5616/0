package com.zemcho.guzhe.controller.app.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppVersionUpdateParam {
    @NotNull(message = "版本id为空")
    private Integer versionId;
}
