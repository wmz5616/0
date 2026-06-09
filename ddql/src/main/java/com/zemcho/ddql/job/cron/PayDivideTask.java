package com.zemcho.ddql.job.cron;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.enums.OrderTypeEnum;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.other.OtherConfig;
import com.zemcho.ddql.config.tgy_pay.MerchantConfig;
import com.zemcho.ddql.entity.business.Shop;
import com.zemcho.ddql.entity.merchant.Merchant;
import com.zemcho.ddql.entity.order.ExchangeOrder;
import com.zemcho.ddql.entity.order.OrderDivideLog;
import com.zemcho.ddql.entity.personalCenter.ShopOrder;
import com.zemcho.ddql.mapper.business.ShopMapper;
import com.zemcho.ddql.mapper.merchant.MerchantMapper;
import com.zemcho.ddql.mapper.order.ExchangeOrderMapper;
import com.zemcho.ddql.mapper.order.OrderDivideLogMapper;
import com.zemcho.ddql.mapper.order.ShopOrderMapper;
import com.zemcho.ddql.util.BigDecimalUtil;
import com.zemcho.ddql.util.tgy.TgyPayUtil;
import com.zemcho.ddql.util.tgy.dto.PayDivideDto;
import com.zemcho.ddql.util.uuid.OrderNoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HXH
 */
//分账定时任务
@Component
@Slf4j
public class PayDivideTask {
    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Autowired
    private ExchangeOrderMapper exchangeOrderMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private OrderDivideLogMapper orderDivideLogMapper;

    @Autowired
    private OtherConfig otherConfig;

    @Autowired
    private MerchantConfig merchantConfig;

    @Autowired
    private TgyPayUtil tgyPayUtil;

    @Value("${tbg.handling.rate}")
    private Double tbgRate;

    /**
     * 每天凌晨1点进行分账处理
     */
    @Scheduled(cron = "0 0 1 * * ?")
    private void execute() {
        log.info("PayDivideTask 开始执行分账定时任务");

        try {
            LocalDateTime timeLimit = LocalDateTime.now().minusDays(otherConfig.getDivideTimeLimit());

            // 处理门店订单分账
            processOrderDivide(OrderTypeEnum.SHOP, timeLimit);

            // 处理商城订单分账
            processOrderDivide(OrderTypeEnum.EXCHANGE, timeLimit);

            log.info("PayDivideTask 分账定时任务执行完成");
        } catch (Exception e) {
            log.error("PayDivideTask 分账定时任务执行异常", e);
        }
    }

    /**
     * 通用订单分账处理方法
     *
     * @param orderType 订单类型
     * @param timeLimit 时间限制
     */
    private void processOrderDivide(OrderTypeEnum orderType, LocalDateTime timeLimit) {
        log.info("PayDivideTask 开始处理{}分账", orderType.getDesc());

        SearchParam param = new SearchParam();
        
        // 根据订单类型设置不同的查询条件
        if (orderType == OrderTypeEnum.SHOP) {
            param.setSearchIntStatus(1); // 门店订单状态：完成
            param.setSearchField2(0); // 分账状态：未分账
        } else {
            param.setSearchIntStatus(4); // 商城订单状态：已完成
            param.setSearchField4(0); // 分账状态：未分账
        }
        param.setLimitTime(timeLimit);
        
        // 根据订单类型查询订单列表
        if (orderType == OrderTypeEnum.SHOP) {//门店订单
            List<ShopOrder> orderList = shopOrderMapper.selectLists(param);
            if (orderList == null || orderList.isEmpty()) {
                log.info("PayDivideTask 无{}待分账", orderType.getDesc());
                return;
            }

            log.info("PayDivideTask 找到 {} 条{}待分账", orderList.size(), orderType.getDesc());

            for (ShopOrder orderInfo : orderList) {
                try {
                    processShopOrder(orderInfo);
                } catch (Exception e) {
                    log.error("PayDivideTask 处理{}分账异常 id : {}", orderType.getDesc(), orderInfo.getId(), e);
                }
            }
        } else {//商城订单
            List<ExchangeOrder> orderList = exchangeOrderMapper.selectLists(param);
            if (orderList == null || orderList.isEmpty()) {
                log.info("PayDivideTask 无{}待分账", orderType.getDesc());
                return;
            }

            log.info("PayDivideTask 找到 {} 条{}待分账", orderList.size(), orderType.getDesc());

            for (ExchangeOrder orderInfo : orderList) {
                try {
                    processExchangeOrder(orderInfo);
                } catch (Exception e) {
                    log.error("PayDivideTask 处理{}分账异常 id : {}", orderType.getDesc(), orderInfo.getId(), e);
                }
            }
        }
    }

    /**
     * 处理单个门店订单的分账
     */
    private void processShopOrder(ShopOrder orderInfo) {
        Integer shopId = orderInfo.getShopId();
        if (shopId == null || shopId == 0) {
            log.info("PayDivideTask 暂无商家id id : {}", orderInfo.getId());
            return;
        }

        Integer payAmount = orderInfo.getPayAmount();
        if (payAmount == null || payAmount <= 0) {
            log.info("PayDivideTask 订单金额异常 id : {} payAmount : {}", orderInfo.getId(), payAmount);
            return;
        }

        Shop shopInfo = shopMapper.selectById(shopId);
        if (shopInfo == null) {
            log.info("PayDivideTask 获取门店信息失败 id : {} shop_id : {}", orderInfo.getId(), shopId);
            return;
        }

        Integer merchantId = shopInfo.getMerchantId();
        if (merchantId == null || merchantId == 0) {
            log.info("PayDivideTask 门店未绑定商户号 id : {} shop_id : {}", orderInfo.getId(), shopId);
            return;
        }

        Merchant merchantInfo = merchantMapper.selectById(merchantId);
        if (merchantInfo == null) {
            log.info("PayDivideTask 获取商户信息失败 id : {} shop_id : {} merchant_id : {}",
                    orderInfo.getId(), shopId, merchantId);
            return;
        }

        if (merchantInfo.getStatus() == null || merchantInfo.getStatus() != 1) {
            log.info("PayDivideTask 商户未启用 id : {} shop_id : {} merchant_id : {}",
                    orderInfo.getId(), shopId, merchantId);
            return;
        }

        if (merchantInfo.getApplicationStatus() == null ||
                !merchantInfo.getApplicationStatus().equals("COMPLETED")) {
            log.info("PayDivideTask 商户未完成认证 id : {} shop_id : {} merchant_id : {}",
                    orderInfo.getId(), shopId, merchantId);
            return;
        }

        Integer merchantRate = shopInfo.getRate();
        if (merchantRate == null) {
            merchantRate = 0;
        }

        Double platformRate = BigDecimalUtil.divisionDouble(merchantRate, 100, 4);

        Double amount = BigDecimalUtil.divisionDouble(payAmount, 100, 2);

        Double handlingCharge = BigDecimalUtil.multiplicationDouble(amount, tbgRate, 2);

        Double merchantAmount = BigDecimalUtil.multiplicationDouble(amount, 1 - (tbgRate + platformRate), 2);

        Double platformCharge = BigDecimalUtil.subtractionDouble(amount, handlingCharge, 2);
        platformCharge = BigDecimalUtil.subtractionDouble(platformCharge, merchantAmount, 2);

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

        String lowDivideOrderId = OrderNoUtil.generateNo(0);
        PayDivideDto divideData = new PayDivideDto();
        divideData.setAccount(merchantConfig.getAccount());
        divideData.setUpOrderId(orderInfo.getOrderNo());
        divideData.setLowDivideOrderId(lowDivideOrderId);
        divideData.setDivideDetail(JSONObject.toJSONString(divideDetailList));

        Result divideResult = tgyPayUtil.divide(divideData);
        if (!divideResult.success()) {
            log.info("PayDivideTask 分账失败 id : {} shop_id : {} merchant_id : {} ",
                    orderInfo.getId(), shopId, merchantId);
            return;
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

        ShopOrder updateOrder = new ShopOrder();
        updateOrder.setId(orderInfo.getId());
        updateOrder.setDivideStatus(divideStatus);
        updateOrder.setDivideAmount(divideAmount);
        updateOrder.setDivideTime(now);
        updateOrder.setUpdateTime(now);
        shopOrderMapper.update(updateOrder);

        // 记录分账明细，供后台分账明细和小程序结算记录查询使用
        OrderDivideLog divideLog = new OrderDivideLog();
        divideLog.setShopId(shopId);
        divideLog.setShopName(shopInfo.getName());
        divideLog.setOrderType(2);//门店订单
        divideLog.setOrderId(orderInfo.getId());
        divideLog.setOrderNo(orderInfo.getOrderNo());
        divideLog.setAmount(orderInfo.getTotalAmount());
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
        log.info("PayDivideTask 门店订单分账成功 id : {} orderNo : {} divideStatus : {} divideAmount : {}",
                orderInfo.getId(), orderInfo.getOrderNo(), divideStatus, divideAmount);
    }

    /**
     * 处理单个商城订单的分账
     * 商城订单只有现金支付部分需要分账，商品无门店关联，属于平台自营
     */
    private void processExchangeOrder(ExchangeOrder orderInfo) {
        // 检查分账状态，已分账则跳过
        if (orderInfo.getDivideStatus() != null && orderInfo.getDivideStatus() == 1) {
            log.info("PayDivideTask 商城订单已分账 id : {}", orderInfo.getId());
            return;
        }

        // 获取现金支付金额，只有现金支付部分需要分账
        Integer cashAmount = orderInfo.getCashAmount();
        if (cashAmount == null || cashAmount <= 0) {
            log.info("PayDivideTask 商城订单无现金支付金额 id : {} cashAmount : {}", orderInfo.getId(), cashAmount);
            return;
        }

        Double amount = BigDecimalUtil.divisionDouble(cashAmount, 100, 2);

        // 商城订单属于平台自营，全部金额进入平台账户
        // 计算手续费（平台承担）
        Double handlingCharge = BigDecimalUtil.multiplicationDouble(amount, tbgRate, 2);
        Double platformCharge = BigDecimalUtil.subtractionDouble(amount, handlingCharge, 2);

        // 构建分账详情（只有平台账户）
        Map<String, Object> platformDivide = new HashMap<>();
        platformDivide.put("ledgerNo", merchantConfig.getLedgerNo());
        platformDivide.put("ledgerName", merchantConfig.getLedgerName());
        platformDivide.put("amount", platformCharge);

        List<Map> divideDetailList = new ArrayList<>();
        divideDetailList.add(platformDivide);

        String lowDivideOrderId = OrderNoUtil.generateNo(0);
        PayDivideDto divideData = new PayDivideDto();
        divideData.setAccount(merchantConfig.getAccount());
        divideData.setUpOrderId(orderInfo.getUpOrderId() != null ? orderInfo.getUpOrderId() : orderInfo.getOrderNo());
        divideData.setLowDivideOrderId(lowDivideOrderId);
        divideData.setDivideDetail(JSONObject.toJSONString(divideDetailList));

        Result divideResult = tgyPayUtil.divide(divideData);
        if (!divideResult.success()) {
            log.info("PayDivideTask 商城订单分账失败 id : {}", orderInfo.getId());
            return;
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
        Integer divideAmount = BigDecimalUtil.multiplicationDoubleRefundInt(platformCharge, 100);

        // 更新订单分账状态
        ExchangeOrder updateOrder = new ExchangeOrder();
        updateOrder.setId(orderInfo.getId());
        updateOrder.setDivideStatus(divideStatus);
        updateOrder.setDivideAmount(divideAmount);
        updateOrder.setDivideTime(now);
        updateOrder.setUpdateTime(now);
        exchangeOrderMapper.update(updateOrder);

        // 记录分账明细
        OrderDivideLog divideLog = new OrderDivideLog();
        divideLog.setShopId(0);
        divideLog.setShopName("平台自营");
        divideLog.setOrderType(1); // 商城订单
        divideLog.setOrderId(orderInfo.getId());
        divideLog.setOrderNo(orderInfo.getOrderNo());
        divideLog.setAmount(orderInfo.getAmount());
        divideLog.setMerchantNo(merchantConfig.getLedgerNo());
        divideLog.setMerchantName(merchantConfig.getLedgerName());
        divideLog.setDivideNo(lowDivideOrderId);
        divideLog.setDivideAmount(divideAmount);
        divideLog.setHandlingRate(BigDecimalUtil.multiplicationDouble(tbgRate, 100, 1));
        divideLog.setHandlingCharge(BigDecimalUtil.multiplicationDoubleRefundInt(handlingCharge, 100));
        divideLog.setPlatformMerchantNo(merchantConfig.getLedgerNo());
        divideLog.setPlatformMerchantName(merchantConfig.getLedgerName());
        divideLog.setPlatformRate(0D);
        divideLog.setPlatformCharge(divideAmount);
        divideLog.setCreateTime(now);
        orderDivideLogMapper.insert(divideLog);

        log.info("PayDivideTask 商城订单分账成功 id : {} orderNo : {} divideStatus : {} divideAmount : {}",
                orderInfo.getId(), orderInfo.getOrderNo(), divideStatus, divideAmount);
    }
}
