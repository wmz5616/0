package com.zemcho.ddql.job.cron;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.cas.CasUserCoinLog;
import com.zemcho.ddql.entity.cas.CasUserWithdrawal;
import com.zemcho.ddql.mapper.cas.CasUserCoinLogMapper;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.mapper.cas.CasUserWithdrawalMapper;
import com.zemcho.ddql.mapper.team.TeamMapper;
import com.zemcho.ddql.mapper.team.TeamUserMapper;
import com.zemcho.ddql.util.wechatpay.WechatPayUtil;
import com.zemcho.ddql.util.wechatpay.dto.TransferDetailEntityNew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @title: UserWithdrawalStateUpdateTask
 * @Description: 用户提现状态更新任务
 * @Date: 2025/10/10 10:18
 */
@Component
public class UserWithdrawalStateUpdateTask {
    @Autowired
    private CasUserWithdrawalMapper casUserWithdrawalMapper;

    @Autowired
    CasUserMapper casUserMapper;

    @Autowired
    TeamUserMapper teamUserMapper;

    @Autowired
    TeamMapper teamMapper;

    @Autowired
    private CasUserCoinLogMapper casUserCoinLogMapper;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private TransactionDefinition transactionDefinition;

    @Autowired
    private WechatPayUtil wechatPayUtil;

    /**
     * 每10分钟执行一次
     */
    @Scheduled(fixedRate = 600000)
    public void execute() {
        //获取未转账完的记录
        SearchParam param = new SearchParam();
        param.setSearchStrList(List.of("ACCEPTED", "PROCESSING", "WAIT_USER_CONFIRM", "TRANSFERING", "CANCELING"));
        List<CasUserWithdrawal> withdrawalList = casUserWithdrawalMapper.selectList(param);
        if (withdrawalList != null && !withdrawalList.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (CasUserWithdrawal withdrawal : withdrawalList) {
                TransferDetailEntityNew wxResponse =
                        wechatPayUtil.getTransferDetailByOutNoNew(withdrawal.getOutBillNo());
                if (wxResponse != null) {
                    // 开启事务
                    TransactionStatus transactionStatus =
                            platformTransactionManager.getTransaction(transactionDefinition);
                    try {
                        String state = wxResponse.getState();
                        CasUserWithdrawal withdrawalUpdate = new CasUserWithdrawal();
                        withdrawalUpdate.setId(withdrawal.getId());
                        withdrawalUpdate.setState(state);
                        casUserWithdrawalMapper.update(withdrawalUpdate);

                        //转账失败，更新用户健康币
                        if (state.equals("FAIL")) {
                            //用户团体健康币更新
                            teamUserMapper.incCoin(withdrawal.getUserId(), withdrawal.getTeamId(),
                                    withdrawal.getAmount());

                            //用户健康币更新
                            casUserMapper.incCoin(withdrawal.getUserId(), null, withdrawal.getAmount());

                            //团体健康币余额更新
                            teamMapper.incCoin(withdrawal.getTeamId(), withdrawal.getAmount());

                            //添加用户健康币变更记录
                            List<CasUserCoinLog> logList = new ArrayList<>();
                            CasUserCoinLog healthCoinLog = new CasUserCoinLog();
                            healthCoinLog.setTxnType(4);
                            healthCoinLog.setTxnId(withdrawal.getId());
                            healthCoinLog.setCoinType(1);
                            healthCoinLog.setNumType(1);
                            healthCoinLog.setCoinNum(withdrawal.getAmount());
                            healthCoinLog.setUserId(withdrawal.getUserId());
                            healthCoinLog.setPhone(withdrawal.getPhone());
                            healthCoinLog.setNickName(withdrawal.getNickname());
                            healthCoinLog.setTeamId(withdrawal.getTeamId());
                            healthCoinLog.setTeamName(withdrawal.getTeamName());
                            healthCoinLog.setTeamType(withdrawal.getTeamType());
                            healthCoinLog.setRemark("商家转账失败，健康币增加" + withdrawal.getAmount());
                            healthCoinLog.setCreateTime(now);
                            logList.add(healthCoinLog);
                            casUserCoinLogMapper.insertAll(logList);
                        }

                        // 事务提交
                        platformTransactionManager.commit(transactionStatus);
                    } catch (Exception e) {
                        // 事务回滚
                        platformTransactionManager.rollback(transactionStatus);

                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
