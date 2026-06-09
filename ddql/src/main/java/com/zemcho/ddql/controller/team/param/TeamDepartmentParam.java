package com.zemcho.ddql.controller.team.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 团队部门参数
 */
@Data
public class TeamDepartmentParam {

    /**
     * 部门ID（更新时必填）
     */
    private Integer id;

    /**
     * 所属团队ID（创建时必填）
     */
    private Integer teamId;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    private String name;

    /**
     * 排序，越小越靠前
     */
    private Integer sort;

    /**
     * 状态：0启用，1禁用
     */
    private Integer status;
}
