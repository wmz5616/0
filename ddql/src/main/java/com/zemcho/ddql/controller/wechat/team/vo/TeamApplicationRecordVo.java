package com.zemcho.ddql.controller.wechat.team.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @title: TeamApplicationRecordVo
 * @Description:
 * @Date: 2025/10/16 20:04
 */
@Data
public class TeamApplicationRecordVo {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 申请加入人的id
     */
    private Integer userId;

    private String avatar;

    private String nickName;

    /**
     * 申请加入的团队的id
     */
    private Integer teamId;

    /**
     * 申请加入的部门id
     */
    private Integer departmentId;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 申请人的名称
     */
    private String userName;

    /**
     * 申请人的电话
     */
    private String userPhone;

    /**
     * 审核状态: 0 未审核, 1 审核通过, 2 审核驳回
     */
    private Integer status;

    /**
     * 加入的方式: 0 申请加入, 1 扫码加入
     */
    private Integer joinType;

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
}
