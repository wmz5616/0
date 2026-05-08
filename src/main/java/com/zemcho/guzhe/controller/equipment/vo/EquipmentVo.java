package com.zemcho.guzhe.controller.equipment.vo;

import com.zemcho.guzhe.entity.equipment.Equipment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentVo extends Equipment {
    // 关联的打卡场所名称
    private String checkInPlaceName;
}
