package com.zemcho.guzhe.controller.common.vo;

import lombok.Data;

/**
 * @title: ShopCommonVo
 * @Description:
 * @Date: 2025/10/16 15:15
 */
@Data
public class ShopCommonVo {
    // 主键ID
    private Integer id;

    // 店铺名称
    private String name;

    // 店铺启用状态 0禁用 1启用
    private Integer status;
}
