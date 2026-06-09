package com.zemcho.ddql.controller.team.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 切换多部门管理开关参数
 */
@Data
public class ToggleMultiDepartmentParam {

    /**
     * 团队ID
     */
    @NotNull(message = "团队ID不能为空")
    private Integer teamId;

    /**
     * 是否多部门管理：0关闭，1开启
     */
    @NotNull(message = "状态不能为空")
    private Integer isMultiDepartment;
}
