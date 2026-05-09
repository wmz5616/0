package com.zemcho.guzhe.controller.equipment.vo;

import lombok.Data;

/**
 * 设备店位租用展示
 */
@Data
public class EquipmentRentalDisplayVo {
    /**
     * 设备ID
     */
    private Integer equipmentId;

    /**
     * 设备编号
     */
    private String serialNumber;

    /**
     * 所属商超ID
     */
    private Integer businessCircleId;

    /**
     * 所属商超名称
     */
    private String businessCircleName;

    /**
     * 总店位数
     */
    private Integer slotTotal;

    /**
     * 已租用店位数
     */
    private Integer usedCount;

    /**
     * 剩余店位数
     */
    private Integer remainingCount;

    /**
     * 当前月份已租用该设备店位的商家名称
     */
    private String merchantNames;
}
