package com.zemcho.guzhe.entity.equipment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @title: EquipmentLog
 * @Description:
 * @Date: 2025/10/15 14:42
 */
@Data
public class EquipmentLog {
    //id
    private Integer id;

    //设备 id
    private Integer equipmentId;

    //状态: 1在线、2离线
    private Integer status;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}
