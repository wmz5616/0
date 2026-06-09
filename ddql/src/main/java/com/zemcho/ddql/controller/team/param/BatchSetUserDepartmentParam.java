package com.zemcho.ddql.controller.team.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 批量设置成员部门参数
 */
@Data
public class BatchSetUserDepartmentParam {

    /**
     * 团队ID
     */
    @NotNull(message = "团队ID不能为空")
    private Integer teamId;

    /**
     * 目标部门ID
     */
    //@NotNull(message = "部门ID不能为空")
    private Integer departmentId;

    /**
     * 需要批量编辑的团队成员记录ID列表
     */
    @NotEmpty(message = "请选择需要编辑的成员")
    private List<Integer> teamUserIds;
}
