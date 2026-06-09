package com.zemcho.guzhe.controller.equipment.param;

import lombok.Data;

/**
 * 设备店位租用展示查询参数
 */
@Data
public class EquipmentRentalDisplayParam {
    /**
     * 查询月份，格式 yyyy-MM；为空时默认当前月份
     */
    private String month;

    /**
     * 设备编号，支持模糊查询
     */
    private String serialNumber;

    /**
     * 所属商超ID
     */
    private Integer businessCircleId;
}
