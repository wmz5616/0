package com.zemcho.ddql.entity.team;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * 团队申请加入记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamApplicationRecord {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 申请加入人的id
     */
    private Integer userId;

    /**
     * 申请加入的团队的id
     */
    private Integer teamId;

    /**
     * 申请加入的部门的id
     */
    private Integer departmentId;

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

    /**
     * 删除时间 (记录被删除或失效时间)
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deleteTime;
}
