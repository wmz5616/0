package com.zemcho.guzhe.controller.login.vo;

import com.zemcho.guzhe.entity.cas.CasAdmin;
import lombok.Data;

import java.util.List;

@Data
public class AdminInfoVO {
    //用户id
    private Integer id;

    //账号
    private String account;

    //名称
    private String name;

    // 状态：0禁用、1启用
    private Integer status;

    //角色id
    private List<Integer> roleIds;

    //菜单id
    private List<Integer> ruleIds;

    public static AdminInfoVO getInstance(CasAdmin adminInfo) {
        AdminInfoVO vo = new AdminInfoVO();
        vo.setId(adminInfo.getId());
        vo.setAccount(adminInfo.getAccount());
        vo.setName(adminInfo.getName());
        vo.setStatus(adminInfo.getStatus());

        return vo;
    }
}