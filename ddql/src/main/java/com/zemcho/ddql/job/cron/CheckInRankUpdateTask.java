package com.zemcho.ddql.job.cron;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.cas.CasUserCheckInTeamRecord;
import com.zemcho.ddql.entity.cas.CasUserSportRecord;
import com.zemcho.ddql.mapper.cas.CasUserCheckInTeamRecordMapper;
import com.zemcho.ddql.mapper.cas.CasUserSportRecordMapper;
import com.zemcho.ddql.util.Constant;
import com.zemcho.ddql.util.redis.RedisUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @title: CheckInRankUpdateTask
 * @Description: 用户打卡团队排名更新任务，只更新昨天的数据，当天的排名从redis获取
 * @Date: 2025/10/9 18:47
 */
@Component
public class CheckInRankUpdateTask {
    @Autowired
    CasUserCheckInTeamRecordMapper casUserCheckInTeamRecordMapper;

    @Autowired
    CasUserSportRecordMapper casUserSportRecordMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 初始化今日的打卡团队排名
     */
    @PostConstruct
    public void initTodayCheckInRankRedis() {
        LocalDate date = LocalDateTime.now().toLocalDate();

        SearchParam searchParam = new SearchParam();
        searchParam.setSearchDate(date);
        List<CasUserCheckInTeamRecord> list = casUserCheckInTeamRecordMapper.selectLists(searchParam);
        if (list != null && !list.isEmpty()) {
            String userTeamRankPrefix = Constant.USER_CHECK_IN_RANK_PREFIX + date + ":";
            for (CasUserCheckInTeamRecord item : list) {
                CasUserSportRecord userSportRecord = casUserSportRecordMapper.selectByUserIdAndDate(item.getUserId(),
                        date);
                if (userSportRecord != null) {
                    redisUtil.zSetAdd(userTeamRankPrefix + item.getTeamId(), item.getUserId(),
                            userSportRecord.getCheckInTime());
                }
            }
        }
    }

    /**
     * 每天凌晨12点执行一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void execute() {
        LocalDate date = LocalDateTime.now().minusHours(1).toLocalDate();

        SearchParam searchParam = new SearchParam();
        searchParam.setSearchDate(date);
        List<CasUserCheckInTeamRecord> list = casUserCheckInTeamRecordMapper.selectLists(searchParam);
        if (list != null && !list.isEmpty()) {
            String userTeamRankPrefix = Constant.USER_CHECK_IN_RANK_PREFIX + date + ":";
            List<Integer> teamIds = new ArrayList<>();
            for (CasUserCheckInTeamRecord item : list) {
                Long rank = redisUtil.zSetRank(userTeamRankPrefix + item.getTeamId(), item.getUserId());
                if (rank != null) {
                    CasUserCheckInTeamRecord recordUpdate = new CasUserCheckInTeamRecord();
                    recordUpdate.setId(item.getId());
                    recordUpdate.setRank(rank.intValue());
                    casUserCheckInTeamRecordMapper.update(recordUpdate);

                    teamIds.add(item.getTeamId());
                }
            }

            // 删除昨日的排名数据
            if (!teamIds.isEmpty()) {
                for (Integer teamId : teamIds) {
                    redisUtil.del(userTeamRankPrefix + teamId);
                }
            }
        }
    }
}
