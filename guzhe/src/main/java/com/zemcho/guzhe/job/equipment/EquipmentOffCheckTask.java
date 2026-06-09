package com.zemcho.guzhe.job.equipment;

import com.zemcho.guzhe.entity.equipment.Equipment;
import com.zemcho.guzhe.entity.equipment.EquipmentLog;
import com.zemcho.guzhe.mapper.equipment.EquipmentLogMapper;
import com.zemcho.guzhe.mapper.equipment.EquipmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备离线检查任务
 */
@Component
public class EquipmentOffCheckTask {
    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private EquipmentLogMapper equipmentLogMapper;

    /**
     * 每分钟检查设备是否离线
     */
    @Scheduled(fixedDelay = 60000)
    public void execute() {
        LocalDateTime checkTime = LocalDateTime.now().minusMinutes(5);
        List<Equipment> offList = equipmentMapper.selectOffList(checkTime);
        if (offList != null && !offList.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (Equipment equipment : offList) {
                Equipment equipmentUpdate = new Equipment();
                equipmentUpdate.setId(equipment.getId());
                equipment.setOnlineStatus(1);
                equipmentMapper.updateByEquipment(equipment);

                EquipmentLog equipmentLog = new EquipmentLog();
                equipmentLog.setEquipmentId(equipment.getId());
                equipmentLog.setStatus(2);
                equipmentLog.setCreatedTime(now);
                equipmentLogMapper.insert(equipmentLog);
            }
        }
    }
}
