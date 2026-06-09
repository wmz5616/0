package com.zemcho.guzhe.job.cron;

import com.zemcho.guzhe.mapper.screen.ScreenRentalDetailMapper;
import com.zemcho.guzhe.mapper.screen.ScreenRentalOrderMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 店位订单状态同步任务。
 * 根据租用年月自动维护待生效、生效中、已完成三个状态。
 */
@Slf4j
@Component
public class ScreenRentalOrderStatusSyncTask {
    @Autowired
    private ScreenRentalDetailMapper screenRentalDetailMapper;

    @Autowired
    private ScreenRentalOrderMapper screenRentalOrderMapper;

    @PostConstruct
    public void init() {
        execute();
    }

    /**
     * 每天凌晨 1 点执行一次，修正跨月后的订单与明细状态。
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void execute() {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        int detailCount = screenRentalDetailMapper.refreshApprovedStatus(currentYear, currentMonth);
        int orderCount = screenRentalOrderMapper.refreshApprovedStatus();

        if (detailCount > 0 || orderCount > 0) {
            log.info("ScreenRentalOrderStatusSyncTask execute success, detailCount={}, orderCount={}, year={}, month={}",
                    detailCount, orderCount, currentYear, currentMonth);
        }
    }
}
