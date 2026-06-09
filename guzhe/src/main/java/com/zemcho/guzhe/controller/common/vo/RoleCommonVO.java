package com.zemcho.guzhe.controller.common.vo;

import lombok.Data;

/**
 * @title: RoleCommonVO
 * @Description:
 * @Date: 2025/5/12 19:12
 */
@Data
public class RoleCommonVO {
    // 主键ID
    private Integer id;

    // 角色名称
    private String name;

    // 状态：0禁用、1启用
    private Integer status;
}
