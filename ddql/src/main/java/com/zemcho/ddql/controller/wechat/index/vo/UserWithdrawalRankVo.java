package com.zemcho.ddql.controller.wechat.index.vo;

import lombok.Data;

/**
 * @title: UserWithdrawalRankVo
 * @Description:
 * @Date: 2025/10/11 17:30
 */
@Data
public class UserWithdrawalRankVo {
    // 用户id
    private Integer userId;

    // 用户昵称
    private String nickName;

    // 用户头像
    private String avatar;

    // 成员在团队下的姓名
    private String userName;

    // 成员在团队下的电话
    private String userPhone;

    // 成员类型: 0 创建者, 1 管理员, 2 普通用户
    private Integer type;

    // 加入的方式: 0 申请加入, 1 扫码加入
    private Integer joinType;

    // 状态: 0 启用, 1 禁用
    private Integer status;

    //提现金额（元）
    private Integer amount;

    //排名
    private Integer rank;
}
