package com.zemcho.ddql.controller.team.param;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamUserSearchParam {

    private Integer teamId;

    private String userName;

    private String userPhone;

    private String keyword;

    // 成员身份类型 0创建者 1管理员 2普通用户
    private Integer type;

    // 状态 0启用1 禁用
    private Integer status;

    // 部门ID
    private Integer departmentId;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
