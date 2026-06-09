package com.zemcho.guzhe.controller.equipment.vo;

import com.zemcho.guzhe.entity.equipment.Equipment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentVo extends Equipment {
    /**
     * 设备截图
     */
    private String screenshotUrl;
}
