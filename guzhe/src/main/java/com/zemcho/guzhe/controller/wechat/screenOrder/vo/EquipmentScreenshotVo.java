package com.zemcho.guzhe.controller.wechat.screenOrder.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备截图返回对象
 */
@Data
public class EquipmentScreenshotVo {
    private Long id;

    private Integer equipmentId;

    private String serialNumber;

    private String screenshotUrl;

    private Integer screenshotStatus;

    private String failReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
