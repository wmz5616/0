package com.zemcho.ddql.job.equipment;

import com.zemcho.ddql.service.equipment.EquipmentPosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 用于更新海报状态
@Component
public class EquipmentPosterStatusTask {

    @Autowired
    private EquipmentPosterService equipmentPosterService;


    // 一分钟执行一次
    @Scheduled(fixedRate = 60000)
    public void updateEquipmentPoster() {
        equipmentPosterService.updateEquipmentPoster();
    }

}
