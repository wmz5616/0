package com.zemcho.guzhe.controller.cas.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @title: AdminListVo
 * @Description:
 * @Date: 2025/5/12 14:28
 */
@Data
public class AdminListVO {
    // 主键ID
    private Integer id;

    // 账号(手机号)
    private String account;

    // 名称
    private String name;

    // 状态：0禁用、1启用
    private Integer status;

    // 备注
    private String remark;

    // 已绑定角色数据
    private List<AdminRoleListVO> roles;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
