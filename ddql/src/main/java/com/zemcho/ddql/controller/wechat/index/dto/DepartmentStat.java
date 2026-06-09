package com.zemcho.ddql.controller.wechat.index.dto;

import lombok.Data;

/**
 * 部门统计数据
 */
@Data
public class DepartmentStat {
    private Integer departmentId;
    private String departmentName;
    private int memberCount;
    private int checkInNum;
    private int healthCoin;
    private int activeMemberCount;
    private float activeRate;
    private float contributionRate;
}