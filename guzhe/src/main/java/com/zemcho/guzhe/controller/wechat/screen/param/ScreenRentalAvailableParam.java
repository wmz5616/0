package com.zemcho.guzhe.controller.wechat.screen.param;

import lombok.Data;

/**
 * 可租用店位查询参数
 */
@Data
public class ScreenRentalAvailableParam {
    /**
     * 商超id，允许为空；为空时查询当前地区下所有商超设备
     */
    private Integer businessCircleId;

    /**
     * 地区/屏幕店地址关键词，businessCircleId 为空时生效
     */
    private String screenAddress;
}
