package com.zemcho.guzhe.controller.wechat.common.vo;

import lombok.Data;

/**
 * @title: TeamCommonVo
 * @Description:
 * @Date: 2025/10/16 14:41
 */
@Data
public class TeamCommonVo {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 团队名字
     */
    private String name;

    /**
     * 团队类型: 0 企事业单位, 1 政府部门, 2 家庭, 3 朋友
     */
    private Integer type;

    /**
     * 是否已认证: 0 未认证, 1 已认证
     */
    private Integer isVerified;

    /**
     * 人数
     */
    private Integer peopleNumber;

    /**
     * 状态: 0 启用, 1 禁用
     */
    private Integer status;

    /**
     * 团队健康币余额
     */
    private Integer healthyCoin;
}
