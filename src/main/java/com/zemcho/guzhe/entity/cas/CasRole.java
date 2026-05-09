package com.zemcho.guzhe.entity.cas;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @title: CasRole
 * @Description: 角色管理表实体类
 * @Date: 2025/5/6 17:05
 */
@Data
public class CasRole {
    // 主键ID
    private Integer id;

    // 角色名称
    private String name;

    // 状态：0禁用、1启用
    private Integer status;

    // 备注
    private String remark;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}