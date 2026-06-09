package com.zemcho.guzhe.job.cron;

import com.zemcho.guzhe.entity.order.ProductOrder;
import com.zemcho.guzhe.entity.product.ProductTicket;
import com.zemcho.guzhe.mapper.order.ProductOrderMapper;
import com.zemcho.guzhe.mapper.product.ProductMapper;
import com.zemcho.guzhe.mapper.product.ProductTicketMapper;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 订单库存延时退库处理任务
 */
@Slf4j
@Component
public class OrderStockDelayRefundJob {
    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductTicketMapper productTicketMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 每10秒执行一次
     */
    @Scheduled(fixedDelay = 10000)
    private void execute() {
        // 获取当前时间戳
        long currentTime = LocalDateTime.now()
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        // 获取所有延时退库的订单
        String key = Constant.ORDER_NUM_REFUND_MONITOR_PREFIX + "productOrder";
        Set<Object> delayOrders = redisUtil.zSetRangeByScore(key, 0, currentTime);

        // 处理延时退库订单
        if (delayOrders != null && !delayOrders.isEmpty()) {
            for (Object orderIdObject : delayOrders) {
                Integer orderId = Integer.valueOf(orderIdObject.toString());

                log.info("OrderStockDelayRefundJob orderId : {} currentTime : {}", orderId, currentTime);

                orderStockRefundCheck(orderId);

                // 从ZSet中删除
                redisUtil.zSetRemove(key, orderId);
            }
        }
    }

    /**
     * 订单延时退库检查
     *
     * @param orderId
     */
    public void orderStockRefundCheck(Integer orderId) {
        ProductOrder orderInfo = productOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            log.error("orderStockRefundCheck 订单不存在:{}", orderId);
            return;
        }
        if (!orderInfo.getStatus().equals(8)) {
            log.error("orderStockRefundCheck 订单状态异常:{} , {}", orderId, orderInfo.getStatus());
            return;
        }

        Integer productId = orderInfo.getProductId();
        Integer num = orderInfo.getNum();

        List<Integer> ticketIds = new ArrayList<>();
        List<String> ticketList = new ArrayList<>();
        if (orderInfo.getIsVirtual() == 1) {
            List<ProductTicket> productTicketList = productTicketMapper.selectByOrderId(orderId, 2);
            num = 0;
            if (productTicketList != null && !productTicketList.isEmpty()) {
                num = productTicketList.size();
                ticketIds = productTicketList.stream().map(ProductTicket::getId).toList();
                ticketList = productTicketList.stream().map(ProductTicket::getTicket).toList();
            }
        }

        log.info("orderStockRefundCheck 订单:{} 退库商品:{} 退库数量:{} 券码id:{} 券码:{}", orderId, productId, num, ticketIds, ticketList);

        //更新商品库存数和销量
        if (num != 0) {
            productMapper.updateStockOrSaleNum(productId, num, -num);
        }

        if (!ticketIds.isEmpty()) {
            productTicketMapper.updateByIds(ticketIds, 1, 0);

            // 重新把券码放回redis列表里
            String key = Constant.PRODUCT_TICKET_LIST + productId;
            redisUtil.rightPushAll(key, ticketList);
        }
    }
}
