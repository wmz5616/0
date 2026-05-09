package com.zemcho.guzhe.job.cron;


import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.cas.vo.CasUserVo;
import com.zemcho.guzhe.entity.cas.CasUser;
import com.zemcho.guzhe.mapper.cas.CasUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

// 给用户定时解锁
@Component
public class UserUnLockTask {

    @Autowired
    CasUserMapper casUserMapper;

    // 每天凌晨12点执行
    @Scheduled(cron = "0 0 0 * * ?")
    public void userUnLockTask() {
        SearchParam param = new SearchParam();
        param.setSearchField1(1);
        param.setEndTime2(LocalDateTime.now());
        List<CasUserVo> unLockList = casUserMapper.selectLists(param);
        for (CasUser casUser : unLockList) {
            casUser.setLock(0);
            casUser.setLockExpiredAt(null);
            casUserMapper.update(casUser);
        }
    }

}
