package com.zemcho.guzhe.entity.equipment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备截图
 */
@Data
public class EquipmentScreenshot {
    private Long id;

    private Integer equipmentId;

    private String serialNumber;

    private String screenshotUrl;

    /**
     * 截图状态：0-待截图 1-成功 2-失败
     */
    private Integer screenshotStatus;

    private String failReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
