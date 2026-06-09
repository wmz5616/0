package com.zemcho.guzhe.common.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author Ryan
 * @title: ChangeOneParam
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/10/14 0014 15:23
 */
@Data
public class ChangeOneParam {
    @NotNull(message = "参数{changeId}为空")
    private Integer changeId;

    private Integer status = 1;
}
