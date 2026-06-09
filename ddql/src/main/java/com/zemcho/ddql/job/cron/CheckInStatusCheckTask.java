package com.zemcho.ddql.job.cron;

import com.zemcho.ddql.mapper.cas.CasUserCheckInRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @title: CheckInStatusCheckTask
 * @Description: 打卡记录失效检测任务
 * @Date: 2025/10/9 15:18
 */
@Component
public class CheckInStatusCheckTask {
    @Autowired
    CasUserCheckInRecordMapper casUserCheckInRecordMapper;

    /**
     * 每天凌晨12点执行一次，打卡记录在该时间内未完成则失效
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void execute() {
        LocalDate checkDate = LocalDateTime.now().minusHours(1).toLocalDate();
        casUserCheckInRecordMapper.invalidCheckIn(checkDate);
    }
}
