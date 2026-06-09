package com.zemcho.ddql.job.cron;

import com.zemcho.ddql.mapper.business.ShopMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @title: ShopTopStatusCheckTask
 * @Description: 店铺置顶状态检查任务
 * @Date: 2025/11/21 19:47
 */
@Component
public class ShopTopStatusCheckTask {
    @Autowired
    private ShopMapper shopMapper;

    /**
     * 初始化店铺置顶状态
     */
    @PostConstruct
    public void initCheckShopTopStatus() {
        LocalDateTime checkTime = LocalDateTime.now().with(LocalTime.MIN);

        shopMapper.checkUpdateTopStatus(checkTime);
    }

    /**
     * 每天凌晨12点执行一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void execute() {
        LocalDateTime checkTime = LocalDateTime.now();

        shopMapper.checkUpdateTopStatus(checkTime);
    }
}
