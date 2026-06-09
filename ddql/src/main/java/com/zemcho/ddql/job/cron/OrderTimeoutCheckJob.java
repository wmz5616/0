package com.zemcho.ddql.job.cron;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.order.ExchangeOrder;
import com.zemcho.ddql.entity.order.RechargeOrder;
import com.zemcho.ddql.mapper.order.ExchangeOrderMapper;
import com.zemcho.ddql.mapper.order.RechargeOrderMapper;
import com.zemcho.ddql.util.Constant;
import com.zemcho.ddql.util.redis.RedisUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

/**
 * 订单超时取消检查任务
 */
@Slf4j
@Component
public class OrderTimeoutCheckJob {
    @Autowired
    private RechargeOrderMapper rechargeOrderMapper;

    @Autowired
    private ExchangeOrderMapper exchangeOrderMapper;

    @Autowired
    private RedisUtil redisUtil;

    @PostConstruct
    public void init() {
        // 启动时全盘扫一次盘
        LocalDateTime timeLimit = LocalDateTime.now().minusMinutes(30);// 目前系统没有配置的功能，先暂时写死30分钟
        SearchParam param = new SearchParam();
        param.setSearchIntStatus(1);
        param.setLimitTime(timeLimit);
        List<RechargeOrder> orderList = rechargeOrderMapper.selectLists(param);
        if (orderList != null && !orderList.isEmpty()) {
            for (RechargeOrder order : orderList) {
                orderTimeoutCheck(order.getId());
            }
        }

        SearchParam exchangeParam = new SearchParam();
        exchangeParam.setSearchIntStatus(0);
        exchangeParam.setLimitTime(timeLimit);
        List<ExchangeOrder> exchangeOrderList = exchangeOrderMapper.selectLists(exchangeParam);
        if (exchangeOrderList != null && !exchangeOrderList.isEmpty()) {
            for (ExchangeOrder order : exchangeOrderList) {
                exchangeOrderTimeoutCheck(order.getId());
            }
        }
    }

    /**
     * 每10秒执行一次
     */
    @Scheduled(fixedRate = 10000)
    private void execute() {
        // 获取当前时间戳
        long currentTime = LocalDateTime.now()
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        // 获取所有超时的订单
        String key = Constant.ORDER_UNPAY_MONITOR;
        Set<Object> expiredOrders = redisUtil.zSetRangeByScore(key, 0, currentTime);

        // 处理超时订单
        if (expiredOrders != null && !expiredOrders.isEmpty()) {
            for (Object orderIdObject : expiredOrders) {
                Integer orderId = Integer.valueOf(orderIdObject.toString());

                log.info("OrderTimeoutCheckJob orderId : {} currentTime : {}", orderId, currentTime);

                orderTimeoutCheck(orderId);

                // 从ZSet中删除
                redisUtil.zSetRemove(key, orderId);
            }
        }

        String exchangeKey = Constant.EXCHANGE_ORDER_UNPAY_MONITOR;
        Set<Object> expiredExchangeOrders = redisUtil.zSetRangeByScore(exchangeKey, 0, currentTime);
        if (expiredExchangeOrders != null && !expiredExchangeOrders.isEmpty()) {
            for (Object orderIdObject : expiredExchangeOrders) {
                Integer orderId = Integer.valueOf(orderIdObject.toString());
                log.info("OrderTimeoutCheckJob exchange orderId : {} currentTime : {}", orderId, currentTime);
                exchangeOrderTimeoutCheck(orderId);
                redisUtil.zSetRemove(exchangeKey, orderId);
            }
        }
    }

    /**
     * 订单超时取消检查
     *
     * @param orderId
     */
    public void orderTimeoutCheck(Integer orderId) {
        RechargeOrder orderInfo = rechargeOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            log.error("orderTimeoutCheck 订单不存在:{}", orderId);
            return;
        }
        if (!orderInfo.getStatus().equals(1)) {
            log.error("orderTimeoutCheck 订单状态异常:{}", orderId);
            return;
        }

        //修改订单相关状态信息
        RechargeOrder orderUpdate = new RechargeOrder();
        orderUpdate.setId(orderInfo.getId());
        orderUpdate.setStatus(3);
        rechargeOrderMapper.update(orderUpdate);
    }

    /**
     * 商品兑换订单超时取消检查
     *
     * @param orderId 订单ID
     */
    public void exchangeOrderTimeoutCheck(Integer orderId) {
        ExchangeOrder orderInfo = exchangeOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            log.error("exchangeOrderTimeoutCheck 订单不存在:{}", orderId);
            return;
        }
        if (!orderInfo.getStatus().equals(0)) {
            log.error("exchangeOrderTimeoutCheck 订单状态异常:{}", orderId);
            return;
        }

        ExchangeOrder orderUpdate = new ExchangeOrder();
        orderUpdate.setId(orderInfo.getId());
        orderUpdate.setStatus(8);
        exchangeOrderMapper.update(orderUpdate);
    }
}
