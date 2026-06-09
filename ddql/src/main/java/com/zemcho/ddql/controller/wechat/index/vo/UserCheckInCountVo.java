package com.zemcho.ddql.controller.wechat.index.vo;

import lombok.Data;

/**
 * @title: UserCheckInCountVo
 * @Description:
 * @Date: 2025/10/9 19:38
 */
@Data
public class UserCheckInCountVo {
    //总打卡次数
    private Integer totalNum;

    //总健康币数量
    private Integer totalHealthCoin;

    //总金币数量
    private Integer totalGoldCoin;
}
