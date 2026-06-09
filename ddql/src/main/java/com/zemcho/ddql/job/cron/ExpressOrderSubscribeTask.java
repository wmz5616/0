package com.zemcho.ddql.job.cron;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.wechat.index.vo.ExchangeOrderAddressVo;
import com.zemcho.ddql.entity.express.ExpressOrder;
import com.zemcho.ddql.mapper.express.ExpressOrderMapper;
import com.zemcho.ddql.mapper.order.ExchangeOrderAddressMapper;
import com.zemcho.ddql.mapper.order.ExchangeOrderMapper;
import com.zemcho.ddql.util.express.KuiDi100Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @title: ExpressOrderSubscribeTask
 * @Description: 快递订阅任务
 * @Date: 2025/10/22 20:14
 */
@Component
@Slf4j
public class ExpressOrderSubscribeTask {
    @Autowired
    ExpressOrderMapper expressOrderMapper;

    @Autowired
    ExchangeOrderMapper exchangeOrderMapper;

    @Autowired
    ExchangeOrderAddressMapper exchangeOrderAddressMapper;

    /**
     * 每15分钟执行一次
     */
    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void execute() {
        SearchParam searchParam = new SearchParam();
        searchParam.setSearchField2(0);
        List<ExpressOrder> expressOrderList = expressOrderMapper.selectLists(searchParam);
        if (expressOrderList == null || expressOrderList.isEmpty()) {
            return;
        }

        for (ExpressOrder expressOrder : expressOrderList) {
            Integer txnType = expressOrder.getTxnType();
            Integer txnId = expressOrder.getTxnId();

            String phone = "";
            switch (txnType) {
                case 1: //商品兑换订单
                    ExchangeOrderAddressVo orderAddress = exchangeOrderAddressMapper.selectByOrderId(txnId);
                    if (orderAddress != null) {
                        phone = orderAddress.getPhone();
                    }
                    break;
                default:
                    break;
            }
            if (phone == null || phone.isEmpty()) {
                continue;
            }

            Boolean result = KuiDi100Util.subscribe(expressOrder.getExpressCompanyCode(), expressOrder.getExpressNo(),
                    phone);
            log.info("快递订阅 expressCompanyCode : {} expressNo : {} result: {}", expressOrder.getExpressCompanyCode(),
                    expressOrder.getExpressNo(), result);

            if (result) {
                ExpressOrder updateOrder = new ExpressOrder();
                updateOrder.setId(expressOrder.getId());
                updateOrder.setIsSub(1);
                expressOrderMapper.update(updateOrder);
            }
        }
    }
}
