package com.zemcho.guzhe.job.cron;

import com.alibaba.fastjson.JSONObject;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.config.other.OtherConfig;
import com.zemcho.guzhe.config.tgy_pay.MerchantConfig;
import com.zemcho.guzhe.entity.merchant.Merchant;
import com.zemcho.guzhe.entity.order.Order;
import com.zemcho.guzhe.entity.order.ProductOrder;
import com.zemcho.guzhe.entity.shop.Shop;
import com.zemcho.guzhe.entity.sys.OrderDivideLog;
import com.zemcho.guzhe.mapper.merchant.MerchantMapper;
import com.zemcho.guzhe.mapper.order.OrderMapper;
import com.zemcho.guzhe.mapper.order.ProductOrderMapper;
import com.zemcho.guzhe.mapper.shop.ShopMapper;
import com.zemcho.guzhe.mapper.sys.OrderDivideLogMapper;
import com.zemcho.guzhe.util.BigDecimalUtil;
import com.zemcho.guzhe.util.tgy.TgyPayUtil;
import com.zemcho.guzhe.util.tgy.dto.PayDivideDto;
import com.zemcho.guzhe.util.uuid.OrderNoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @title: PayDivideTask
 * @Description: 分账定时任务
 * @Date: 2026/5/8 11:30
 */
@Component
@Slf4j
public class PayDivideTask {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private OtherConfig otherConfig;

    @Autowired
    private MerchantConfig merchantConfig;

    @Autowired
    private TgyPayUtil tgyPayUtil;

    @Autowired
    private OrderDivideLogMapper orderDivideLogMapper;

    @Value("${tbg.handling.rate}")
    private Double tbgRate;

    /**
     * 每天凌晨1点进行分账处理
     */
    @Scheduled(cron = "0 0 1 * * ?")
    private void execute() {
        // 获取当前所有需分账的订单
        LocalDateTime timeLimit = LocalDateTime.now().minusDays(otherConfig.getDivideTimeLimit());
        SearchParam param = new SearchParam();
        param.setSearchIntStatus(1);
        param.setSearchField6(0d);
        param.setLimitTime(timeLimit);
        List<Order> orderList = orderMapper.selectLists(param);
        if (orderList == null || orderList.isEmpty()) {
            log.info("PayDivideTask 无可分账订单数据");
            return;
        }

        for (Order orderInfo : orderList) {
            Integer shopId = orderInfo.getShopId();
            if (shopId == null || shopId == 0) {
                log.info("PayDivideTask 暂无商家id id : {}", orderInfo.getId());
                continue;
            }

            if (orderInfo.getAmount() <= 0) {
                continue;
            }

            Shop shopInfo = shopMapper.selectById(shopId);
            if (shopInfo == null) {
                log.info("PayDivideTask 获取门店信息失败 id : {} shop_id : {}", orderInfo.getId(), shopId);
                continue;
            }
            Integer merchantId = shopInfo.getMerchantId();
            if (merchantId == null || merchantId == 0) {
                log.info("PayDivideTask 门店未绑定商户号 id : {} shop_id : {}", orderInfo.getId(), shopId);
                continue;
            }
            Double rate = shopInfo.getRate();
            if (rate == null || rate <= 0 || rate > 100) {
                log.info("PayDivideTask 门店平台费率有误 id : {} shop_id : {} rate : {}", orderInfo.getId(), shopId, rate);
                continue;
            }
            rate = BigDecimalUtil.divisionDouble(rate, 100, 3);

            Merchant merchantInfo = merchantMapper.selectById(merchantId);
            if (merchantInfo == null) {
                log.info("PayDivideTask 获取商户信息失败 id : {} shop_id : {} merchant_id : {}", orderInfo.getId(),
                        shopId, merchantId);
                continue;
            }
            if (merchantInfo.getStatus() == null || merchantInfo.getStatus() != 1) {
                log.info("PayDivideTask 商户未启用 id : {} shop_id : {} merchant_id : {}", orderInfo.getId(),
                        shopId, merchantId);
                continue;
            }
            if (merchantInfo.getApplicationStatus() == null ||
                    !merchantInfo.getApplicationStatus().equals("COMPLETED")) {
                log.info("PayDivideTask 商户未完成认证 id : {} shop_id : {} merchant_id : {}", orderInfo.getId(),
                        shopId, merchantId);
                continue;
            }

            // 将订单金额转为元
            Double amount = BigDecimalUtil.divisionDouble(orderInfo.getAmount(), 100, 2);

            //通莞手续费=订单金额*通莞费率
            Double handlingCharge = BigDecimalUtil.multiplicationDouble(amount, tbgRate, 2);

            //商家分账金额=订单金额*（1-（通莞费率+平台费率））
            Double merchantAmount = BigDecimalUtil.multiplicationDouble(amount, 1 - (tbgRate + rate), 2);

            //平台收费=订单金额-通莞手续费-商家分账金额
            Double platformCharge = BigDecimalUtil.subtractionDouble(amount, handlingCharge, 2);
            platformCharge = BigDecimalUtil.subtractionDouble(platformCharge, merchantAmount, 2);

            //分账详情信息
            Map<String, Object> merchantDivide = new HashMap<>();
            merchantDivide.put("ledgerNo", merchantInfo.getMerchantNo());
            merchantDivide.put("ledgerName", merchantInfo.getMerchantName());
            merchantDivide.put("amount", merchantAmount);
            Map<String, Object> platformDivide = new HashMap<>();
            platformDivide.put("ledgerNo", merchantConfig.getLedgerNo());
            platformDivide.put("ledgerName", merchantConfig.getLedgerName());
            platformDivide.put("amount", platformCharge);
            List<Map> divideDetailList = new ArrayList<>();
            divideDetailList.add(merchantDivide);
            divideDetailList.add(platformDivide);

            // 分账处理
            //因为请求分账接口，不管接口返回成功还是失败lowDivideOrderId都不能重复提交，所以这里单独再生成一个
            String lowDivideOrderId = OrderNoUtil.generateNo(0);
            PayDivideDto divideData = new PayDivideDto();
            divideData.setAccount(merchantConfig.getAccount());
            divideData.setUpOrderId(orderInfo.getUpOrderNo());
            divideData.setLowDivideOrderId(lowDivideOrderId);
            divideData.setDivideDetail(JSONObject.toJSONString(divideDetailList));
            Result divideResult = tgyPayUtil.divide(divideData);
            if (!divideResult.success()) {
                log.info("PayDivideTask 分账失败 id : {} shop_id : {} merchant_id : {}", orderInfo.getId(),
                        shopId, merchantId);
                continue;
            }
            Map<String, Object> divideResultData = (Map<String, Object>) divideResult.getData();
            Integer divideStatus = 1;
            if (divideResultData.get("state") != null) {
                divideStatus = Integer.valueOf(divideResultData.get("state").toString());
                if (divideStatus == 0) {
                    divideStatus = 1;
                }
            }

            LocalDateTime now = LocalDateTime.now();
            Integer divideAmount = BigDecimalUtil.multiplicationDoubleRefundInt(merchantAmount, 100);

            //更新订单分账信息
            Order updateOrder = new Order();
            updateOrder.setId(orderInfo.getId());
            updateOrder.setDivideStatus(divideStatus);
            updateOrder.setDivideAmount(divideAmount);
            updateOrder.setDivideTime(now);
            orderMapper.update(updateOrder);

            switch (orderInfo.getOrderType()) {
                case 1:  //商品订单
                    ProductOrder updateProductOrder = new ProductOrder();
                    updateProductOrder.setId(orderInfo.getOrderId());
                    updateProductOrder.setDivideStatus(divideStatus);
                    updateProductOrder.setDivideAmount(divideAmount);
                    updateProductOrder.setDivideTime(now);
                    productOrderMapper.update(updateProductOrder);
                    break;
                default:
                    log.info("PayDivideTask 未知订单类型 id : {} shop_id : {} merchant_id : {} order_type : {}",
                            orderInfo.getId(), shopId, merchantId, orderInfo.getOrderType());
                    break;
            }

            // 记录分账明细，供后台分账明细和小程序结算记录查询使用
            OrderDivideLog divideLog = new OrderDivideLog();
            divideLog.setShopId(shopId);
            divideLog.setShopName(shopInfo.getName());
            divideLog.setOrderType(orderInfo.getOrderType());
            divideLog.setOrderId(orderInfo.getOrderId());
            divideLog.setOrderNo(orderInfo.getOrderNo());
            divideLog.setAmount(orderInfo.getAmount());
            divideLog.setMerchantNo(merchantInfo.getMerchantNo());
            divideLog.setMerchantName(merchantInfo.getMerchantName());
            divideLog.setDivideNo(lowDivideOrderId);
            divideLog.setDivideAmount(divideAmount);
            divideLog.setHandlingRate(BigDecimalUtil.multiplicationDouble(tbgRate, 100, 1));
            divideLog.setHandlingCharge(BigDecimalUtil.multiplicationDoubleRefundInt(handlingCharge, 100));
            divideLog.setPlatformMerchantNo(merchantConfig.getLedgerNo());
            divideLog.setPlatformMerchantName(merchantConfig.getLedgerName());
            divideLog.setPlatformRate(shopInfo.getRate() == null ? 0D : shopInfo.getRate());
            divideLog.setPlatformCharge(BigDecimalUtil.multiplicationDoubleRefundInt(platformCharge, 100));
            divideLog.setCreateTime(now);
            orderDivideLogMapper.insert(divideLog);

        }
    }
}
