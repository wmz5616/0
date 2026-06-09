package com.zemcho.ddql.controller.wechat.index.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckInParam {
    @NotNull(message = "打卡方式为空")
    private Integer checkInMethod;

    @NotNull(message = "场地id为空")
    private Integer placeId;

    // 设备id，扫码打卡时必须
    private Integer equipmentId = 0;

    private String userLocation;
}
