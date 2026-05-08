package com.zemcho.guzhe.controller.common.vo;

import lombok.Data;

/**
 * @title: SupermarketCommonVo
 * @Description:
 * @Date: 2026/5/8 9:17
 */
@Data
public class SupermarketCommonVo {
    // 商超id
    private Integer id;

    // 商超名称
    private String name;

    // 状态 0禁用 1启用
    private Integer status;
}
