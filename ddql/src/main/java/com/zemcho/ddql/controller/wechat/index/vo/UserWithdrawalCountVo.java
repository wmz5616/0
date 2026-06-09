package com.zemcho.ddql.controller.wechat.index.vo;

import lombok.Data;

/**
 * @title: UserWithdrawalRankVo
 * @Description:
 * @Date: 2025/10/11 17:30
 */
@Data
public class UserWithdrawalCountVo {
    // 用户id
    private Integer userId;

    //提现金额（元）
    private Integer amount;
}
