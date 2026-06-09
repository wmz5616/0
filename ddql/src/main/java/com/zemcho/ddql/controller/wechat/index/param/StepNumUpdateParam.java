package com.zemcho.ddql.controller.wechat.index.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StepNumUpdateParam {
    @NotNull(message = "步数为空")
    private Integer stepNum;
}
