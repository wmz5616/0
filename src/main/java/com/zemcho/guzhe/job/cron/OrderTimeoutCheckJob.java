package com.zemcho.guzhe.job.cron;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.entity.order.ProductOrder;
import com.zemcho.guzhe.mapper.order.ProductOrderMapper;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.redis.RedisUtil;
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
    private ProductOrderMapper productOrderMapper;

    @Autowired
    private RedisUtil redisUtil;

    @PostConstruct
    public void init() {
        // 启动时全盘扫一次盘
        LocalDateTime timeLimit = LocalDateTime.now().minusMinutes(30);// 目前系统没有配置的功能，先暂时写死30分钟
        SearchParam param = new SearchParam();
        param.setSearchIntStatus(0);
        param.setLimitTime(timeLimit);
        List<ProductOrder> orderList = productOrderMapper.selectLists(param);
        if (orderList != null && !orderList.isEmpty()) {
            for (ProductOrder order : orderList) {
                orderTimeoutCheck(order.getId());
            }
        }
    }

    /**
     * 每10秒执行一次
     */
    @Scheduled(fixedDelay = 10000)
    private void execute() {
        // 获取当前时间戳
        long currentTime = LocalDateTime.now()
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        // 获取所有超时的订单
        String key = Constant.ORDER_UNPAY_MONITOR_PREFIX + "productOrder";
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
    }

    /**
     * 订单超时取消检查
     *
     * @param orderId
     */
    public void orderTimeoutCheck(Integer orderId) {
        ProductOrder orderInfo = productOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            log.error("orderTimeoutCheck 订单不存在:{}", orderId);
            return;
        }
        if (!orderInfo.getStatus().equals(0)) {
            log.error("orderTimeoutCheck 订单状态异常:{} , {}", orderId, orderInfo.getStatus());
            return;
        }

        //修改订单相关状态信息
        ProductOrder orderUpdate = new ProductOrder();
        orderUpdate.setId(orderInfo.getId());
        orderUpdate.setStatus(8);
        productOrderMapper.update(orderUpdate);
    }
}
