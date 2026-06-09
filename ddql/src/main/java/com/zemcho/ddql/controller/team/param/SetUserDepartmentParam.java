package com.zemcho.ddql.controller.team.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 设置成员部门参数
 */
@Data
public class SetUserDepartmentParam {

    /**
     * 团队成员记录ID
     */
    @NotNull(message = "团队成员ID不能为空")
    private Integer teamUserId;

    /**
     * 部门ID（null表示移除部门）
     */
    private Integer departmentId;
}
