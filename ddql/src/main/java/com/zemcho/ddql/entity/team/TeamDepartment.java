package com.zemcho.ddql.entity.team;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 团队部门实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDepartment {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 所属团队ID
     */
    private Integer teamId;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 排序，越小越靠前
     */
    private Integer sort;

    /**
     * 状态：1启用，0禁用
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
     * 删除时间（软删除）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deleteTime;

    /**
     * 部门人数（非数据库字段）
     */
    private Integer count;
}
