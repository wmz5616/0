package com.zemcho.ddql.controller.team.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamSearchParam {
    /**
     * 团队名字
     */
    private String name;

    /**
     * 团队类型: 0 企事业单位, 1 政府部门, 2 家庭, 3 朋友
     */
    private Integer type;

    /**
     * 是否已认证: 0 未认证, 1 审核中, 2 审核驳回, 3 已认证
     */
    private Integer isVerified;

    /**
     * 认证审核状态: 0 审核中, 1 审核通过, 2 审核驳回
     */
    private Integer verificationStatus;

    /**
     * 状态: 0 启用, 1 禁用
     */
    private Integer status;

    /**
     * 地区
     */
    private String region;

    /**
     * 创建时间开始查询时间
     */
    private String createBeginTime;

    /**
     * 创建时间结束查询时间
     */
    private String createEndTime;

    /**
     * 修改时间开始查询时间
     */
    private String deleteBeginTime;

    /**
     * 删除时间结束查询时间
     */
    private String deleteEndTime;

    private Integer pageNum = 1;

    private Integer pageSize = 10;


    List<Integer> teamIds;
}
