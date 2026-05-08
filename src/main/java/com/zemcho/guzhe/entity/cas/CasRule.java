package com.zemcho.guzhe.entity.cas;

import lombok.Data;

/**
 * @title: CasRule
 * @Description:
 * @Date: 2025/5/6 17:05
 */
@Data
public class CasRule {
    // 主键ID
    private Integer id;

    // 是否是菜单：0否，1是
    private Integer isMenu;

    // 上级菜单id
    private Integer parentId;

    // 名称
    private String ruleName;

    // API路径
    private String api;

    // 排序顺序
    private Integer sort;

    public CasRule(Integer id, Integer isMenu, Integer parentId, String ruleName, String api, Integer sort) {
        this.id = id;
        this.isMenu = isMenu;
        this.parentId = parentId;
        this.ruleName = ruleName;
        this.api = api;
        this.sort = sort;
    }
}