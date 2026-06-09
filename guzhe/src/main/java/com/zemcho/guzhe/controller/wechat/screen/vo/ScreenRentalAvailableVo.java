package com.zemcho.guzhe.controller.wechat.screen.vo;

import lombok.Data;

import java.util.List;

/**
 * 可租用店位信息
 */
@Data
public class ScreenRentalAvailableVo {
    // 设备id
    private Integer equipmentId;

    // 设备编号
    private String serialNumber;

    // 租金
    private Integer money;

    // 商超id
    private Integer businessCircleId;

    // 商超名称
    private String businessCircleName;


    private List<ScreenRentalMonthVo> monthList;
}
