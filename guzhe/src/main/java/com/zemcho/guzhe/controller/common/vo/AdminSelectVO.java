package com.zemcho.guzhe.controller.common.vo;

import lombok.Data;

/**
 * @title: AdminSelectVO
 * @Description:
 * @Date: 2025/5/12 18:34
 */
@Data
public class AdminSelectVO {
    // 主键ID
    private Integer id;

    // 账号
    private String account;

    // 名称
    private String name;

    // 状态：0禁用、1启用
    private Integer status;
}
