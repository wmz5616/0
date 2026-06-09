package com.zemcho.ddql.controller.wechat.index.vo;

import lombok.Data;

/**
 * @title: UserCheckInPlaceTypeStatVo
 * @Description:
 * @Date: 2025/12/18 15:48
 */
@Data
public class UserCheckInPlaceTypeStatVo {
    // 打卡类型id
    private Integer checkInTypeId;

    // 打卡类型名称
    private String checkInTypeName;

    // 打卡类型照片url
    private String checkInTypeImages;

    // 打卡次数
    private Integer checkInNum;
}
