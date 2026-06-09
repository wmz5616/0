package com.zemcho.guzhe.controller.cas.vo;

import lombok.Data;

import java.util.List;

/**
 * @title: RuleTreeVO
 * @Description:
 * @Date: 2025/5/7 14:04
 */
@Data
public class RuleTreeVO {
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

    private Boolean isSelected;

    private List<RuleTreeVO> children;
}
