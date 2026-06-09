package com.zemcho.ddql.job.cron.merchant;

import com.zemcho.ddql.service.merchant.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MerchantQueryTask {

    @Autowired
    private MerchantService merchantService;

    // 每半小时执行一次
    @Scheduled(cron = "0 0/30 * * * ?")
    public void query() {
        merchantService.checkProcess();
    }

}
