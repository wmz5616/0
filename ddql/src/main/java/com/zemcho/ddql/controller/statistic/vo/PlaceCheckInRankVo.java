package com.zemcho.ddql.controller.statistic.vo;

import lombok.Data;

/**
 * @title: PlaceCheckInRankVo
 * @Description:
 * @Date: 2025/11/7 18:45
 */
@Data
public class PlaceCheckInRankVo {
    // 场地id
    private Integer placeId;

    // 场地名称
    private String placeName;

    // 关联的打卡类型id
    private Integer checkInTypeId;

    //关联的打卡类型名称
    private String checkInTypeName;

    //关联设备id
    private Integer equipmentId;

    //关联设备名称
    private String equipmentName;

    //打卡量
    private Integer checkInNum;

    //排名
    private Integer rank;
}
