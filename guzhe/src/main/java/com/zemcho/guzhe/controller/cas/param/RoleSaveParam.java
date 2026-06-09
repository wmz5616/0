package com.zemcho.guzhe.controller.cas.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @title: RoleSaveParam
 * @Description:
 * @Date: 2025/5/7 14:47
 */
@Data
public class RoleSaveParam {
    private Integer id;

    @NotBlank(message = "角色名称为空")
    private String name;

    @NotNull(message = "状态为空")
    private Integer status;

    private String remark = "";
}
