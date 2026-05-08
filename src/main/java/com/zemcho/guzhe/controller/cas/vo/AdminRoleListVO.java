package com.zemcho.guzhe.controller.cas.vo;

import lombok.Data;

/**
 * @title: AdminRoleListVO
 * @Description:
 * @Date: 2025/5/12 14:28
 */
@Data
public class AdminRoleListVO {
    // 主键ID
    private Integer id;

    // 管理员ID
    private Integer adminId;

    // 角色名称
    private String name;

    // 状态：0禁用、1启用
    private Integer status;
}
