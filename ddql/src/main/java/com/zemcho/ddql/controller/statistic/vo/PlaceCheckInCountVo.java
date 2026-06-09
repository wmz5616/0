package com.zemcho.ddql.controller.statistic.vo;

import lombok.Data;

/**
 * @title: PlaceCheckInCountVo
 * @Description:
 * @Date: 2025/11/7 18:50
 */
@Data
public class PlaceCheckInCountVo {
    // 场地id
    private Integer placeId;

    //打卡量
    private Integer checkInNum;
}
