package com.zemcho.ddql.controller.wechat.index.vo;

import lombok.Data;

/**
 * @title: UserCheckInRankCountVo
 * @Description:
 * @Date: 2025/10/11 17:30
 */
@Data
public class UserCheckInRankCountVo {
    // 用户id
    private Integer userId;

    //健康币数量
    private Integer healthCoin;

    // 打卡时长（秒）
    private Integer checkInTime;

    // 打卡次数
    private Integer checkInNum;

    // 打卡场地id，用英文逗号隔开
    private String placeIds;
}
