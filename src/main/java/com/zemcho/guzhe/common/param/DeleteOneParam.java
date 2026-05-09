package com.zemcho.guzhe.common.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author Ryan
 * @title: DeleteOneParam
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/10/28 0028 9:53
 */
@Data
public class DeleteOneParam {
    @NotNull(message = "参数{deleteId}为空")
    private Integer deleteId;
}