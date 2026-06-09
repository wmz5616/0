package com.zemcho.ddql.entity.team;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 团队用户关联实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamUser {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 团队id
     */
    private Integer teamId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 成员类型: 0 创建者, 1 管理员, 2 普通用户
     */
    private Integer type;

    /**
     * 所属部门ID
     */
    private Integer departmentId;

    /**
     * 成员在团队下的姓名
     */
    private String userName;

    /**
     * 成员在团队下的电话
     */
    private String userPhone;

    /**
     * 用户在该团队下的健康币余额
     */
    private Integer healthyCoin;

    /**
     * 加入的方式: 0 申请加入, 1 扫码加入
     */
    private Integer joinType;

    /**
     * 启用状态: 0 启用, 1 禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 删除时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deleteTime;
}
