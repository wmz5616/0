package com.zemcho.ddql.job.unLock;


import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.cas.vo.CasUserVo;
import com.zemcho.ddql.entity.cas.CasUser;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

// 给用户定时解锁
@Component
public class UnLockTask {

    @Autowired
    CasUserMapper casUserMapper;

    // 每天凌晨12点执行
    @Scheduled(cron = "0 0 0 * * ?")
    public void unLock() {
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
