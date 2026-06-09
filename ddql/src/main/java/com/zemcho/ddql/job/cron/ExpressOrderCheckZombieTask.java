package com.zemcho.ddql.job.cron;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.express.ExpressOrder;
import com.zemcho.ddql.entity.order.ExchangeOrder;
import com.zemcho.ddql.mapper.express.ExpressOrderMapper;
import com.zemcho.ddql.mapper.order.ExchangeOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @title: ExpressOrderCheckZombieTask
 * @Description: 检测快递订单是否为僵尸物流：快递100那边3天查不到信息，则会认为快递单号错误，此时我们需要定时清理掉，才能重新录入该单号
 * @Date: 2025/10/22 19:58
 */
@Component
@Slf4j
public class ExpressOrderCheckZombieTask {

    @Autowired
    ExpressOrderMapper expressOrderMapper;

    @Autowired
    ExchangeOrderMapper exchangeOrderMapper;

    /**
     * 每天凌晨1点执行一次
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void execute() {
        LocalDateTime checkTime = LocalDateTime.now().minusDays(3);

        SearchParam searchParam = new SearchParam();
        searchParam.setSearchField3(1);
        searchParam.setLimitTime(checkTime);
        searchParam.setSearchStatus(true);
        List<ExpressOrder> expressOrderList = expressOrderMapper.selectLists(searchParam);
        if (expressOrderList == null || expressOrderList.isEmpty()) {
            return;
        }
        for (ExpressOrder expressOrder : expressOrderList) {
            log.info("检测到僵尸物流订单: {}", expressOrder);

            ExpressOrder updateOrder = new ExpressOrder();
            updateOrder.setId(expressOrder.getId());
            updateOrder.setIsRecord(0);
            expressOrderMapper.update(updateOrder);

            Integer txnType = expressOrder.getTxnType();
            Integer txnId = expressOrder.getTxnId();
            switch (txnType) {
                case 1: //商品兑换订单
                    ExchangeOrder exchangeOrderUpdate = new ExchangeOrder();
                    exchangeOrderUpdate.setId(txnId);
                    exchangeOrderUpdate.setExpressStatus(-2);
                    exchangeOrderMapper.update(exchangeOrderUpdate);
                    break;
                default:
                    break;
            }
        }
    }
}
