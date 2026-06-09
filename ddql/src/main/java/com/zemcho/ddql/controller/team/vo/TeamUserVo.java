package com.zemcho.ddql.controller.team.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.ddql.entity.team.Team;
import com.zemcho.ddql.entity.team.TeamCheckInSettings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamUserVo {

    private Integer id;

    private Integer userId;

    private Integer teamId;

    private Team team;

    private String avatar;

    private String nickName;

    private String userName;

    private String userPhone;

    // 成员身份类型
    private Integer type;

    // 所属部门ID
    private Integer departmentId;

    // 部门名称
    private String departmentName;

    private Integer healthyCoin;

    private Integer joinType;

    private Integer status;

    private TeamCheckInSettings teamCheckInSettings;

    /**
     * 删除时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deleteTime;
}
