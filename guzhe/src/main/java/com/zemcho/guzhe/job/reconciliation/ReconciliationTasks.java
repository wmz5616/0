package com.zemcho.guzhe.job.reconciliation;

import com.zemcho.guzhe.entity.order.Order;
import com.zemcho.guzhe.entity.order.ProductOrder;
import com.zemcho.guzhe.entity.reconciliation.SubLedgerSummary;
import com.zemcho.guzhe.entity.reconciliation.TransactionFlow;
import com.zemcho.guzhe.entity.reconciliation.TransactionSummary;
import com.zemcho.guzhe.controller.sys.param.TransactionFlowSearchParam;
import com.zemcho.guzhe.mapper.order.OrderMapper;
import com.zemcho.guzhe.mapper.order.ProductOrderMapper;
import com.zemcho.guzhe.mapper.sys.TransactionFlowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Component
@Slf4j
public class ReconciliationTasks {

    @Autowired
    private TransactionFlowMapper flowMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Scheduled(cron = "0 5 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void transactionReconciliationTask() {
        transactionReconciliationTask(LocalDate.now().minusDays(1));
    }

    public void transactionReconciliationTask(LocalDate targetDate) {
        String billDate = targetDate.toString();
        log.info("开始执行交易对账统计任务，日期：{}", billDate);

        flowMapper.deleteTransactionFlowByDate(billDate);
        flowMapper.deleteTransactionSummaryByDate(billDate);

        List<Order> orders = orderMapper.selectOrderListForReconciliation(
                targetDate.atStartOfDay(),
                targetDate.atTime(LocalTime.MAX)
        );

        if (orders == null || orders.isEmpty()) {
            log.info("交易对账统计任务执行完成，日期 {} 无订单数据", billDate);
            return;
        }

        List<TransactionFlow> allFlows = new ArrayList<>();
        Map<Integer, TransactionSummary> summaryMap = new HashMap<>();

        for (Order order : orders) {
            int shopId = order.getShopId() != null ? order.getShopId() : 0;

            String productName = "";
            String remark = "";
            Integer orderType = order.getOrderType();

            if (orderType == null && order.getOrderNo() != null && !order.getOrderNo().isEmpty()) {
                ProductOrder productOrder = productOrderMapper.selectByOrderNo(order.getOrderNo(), null);
                if (productOrder != null) {
                    orderType = 1;
                    if (productOrder.getProductName() != null) {
                        productName = productOrder.getProductName();
                    }
                    if (productOrder.getRemark() != null) {
                        remark = productOrder.getRemark();
                    }
                }
            }

            if (orderType != null) {
                switch (orderType) {
                    case 1:
                        if ((productName.isEmpty() || remark.isEmpty()) && order.getOrderId() != null) {
                            ProductOrder productOrder = productOrderMapper.selectById(order.getOrderId());
                            if (productOrder != null) {
                                if (productName.isEmpty() && productOrder.getProductName() != null) {
                                    productName = productOrder.getProductName();
                                }
                                if (remark.isEmpty() && productOrder.getRemark() != null) {
                                    remark = productOrder.getRemark();
                                }
                            }
                        }
                        if ((productName.isEmpty() || remark.isEmpty()) && order.getOrderNo() != null && !order.getOrderNo().isEmpty()) {
                            ProductOrder productOrder = productOrderMapper.selectByOrderNo(order.getOrderNo(), null);
                            if (productOrder != null) {
                                if (productName.isEmpty() && productOrder.getProductName() != null) {
                                    productName = productOrder.getProductName();
                                }
                                if (remark.isEmpty() && productOrder.getRemark() != null) {
                                    remark = productOrder.getRemark();
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

            if (order.getPayTime() != null
                    && order.getPayTime().toLocalDate().equals(targetDate)) {
                TransactionFlow flow = buildFlow(order, targetDate, 1, productName, remark);
                allFlows.add(flow);
                int incomeAmt = order.getAmount() != null ? order.getAmount() : 0;
                accumulateSummary(summaryMap, shopId, targetDate, incomeAmt, 0, order.getDivideAmount(), 0, true);
            }

            if (order.getRefundTime() != null
                    && order.getRefundTime().toLocalDate().equals(targetDate)) {
                TransactionFlow flow = buildFlow(order, targetDate, 2, productName, remark);
                allFlows.add(flow);
                int refundAmt = order.getRefundAmount() != null ? order.getRefundAmount() : 0;
                accumulateSummary(summaryMap, shopId, targetDate, 0, refundAmt, 0, order.getDivideAmount(), false);
            }
        }

        for (TransactionSummary summary : summaryMap.values()) {
            flowMapper.insertTransactionSummary(summary);
        }

        if (!allFlows.isEmpty()) {
            int batchSize = 100;
            for (int i = 0; i < allFlows.size(); i += batchSize) {
                int end = Math.min(i + batchSize, allFlows.size());
                flowMapper.batchInsertTransactionFlow(allFlows.subList(i, end));
            }
        }

        log.info("交易对账统计任务执行完成，共处理 {} 条汇总，{} 条流水", summaryMap.size(), allFlows.size());
    }

    private TransactionFlow buildFlow(Order order, LocalDate targetDate, int type, String productName, String remark) {
        TransactionFlow flow = new TransactionFlow();
        flow.setShopId(order.getShopId() != null ? order.getShopId() : 0);
        flow.setBillDate(targetDate);
        flow.setTransactionTime(type == 1 ? order.getPayTime() : order.getRefundTime());
        flow.setOrderNo(order.getOrderNo() != null ? order.getOrderNo() : "");
        flow.setOrderId(order.getOrderId() != null ? order.getOrderId().longValue() : 0L);
        flow.setOrderType(order.getOrderType());
        flow.setUserId(order.getUserId() != null ? order.getUserId().longValue() : 0L);
        flow.setType(type);
        if (type == 1) {
            flow.setAmount(order.getAmount() != null ? order.getAmount().longValue() : 0L);
        } else {
            flow.setAmount(order.getRefundAmount() != null ? order.getRefundAmount().longValue() : 0L);
        }
        flow.setServiceFee(order.getDivideAmount() != null ? order.getDivideAmount().longValue() : 0L);
        flow.setMerchantOrderNo(order.getUpOrderNo() != null ? order.getUpOrderNo() : "");
        flow.setOrderno(order.getUpOrderNo() != null ? order.getUpOrderNo() : "");
        flow.setOriginMerchantOrderNo("");
        flow.setOriginOrderno("");
        flow.setProductName(productName);
        flow.setTotalQuantity(order.getNum() != null ? order.getNum() : 0);
        String nickName = order.getNickName() != null ? order.getNickName() : "";
        String phone = order.getPhone() != null ? order.getPhone() : "";
        String orderUser = nickName;
        if (!phone.isEmpty()) {
            orderUser += "(" + phone + ")";
        }
        flow.setOrderUser(orderUser);
        flow.setOrderUserPhone(phone);
        flow.setRemark(remark != null ? remark : "");
        return flow;
    }

    private void accumulateSummary(Map<Integer, TransactionSummary> map, int shopId, LocalDate targetDate,
                                   int income, int refund, Integer feeAmount, Integer feeReturnAmount, boolean isIncome) {
        TransactionSummary summary = map.computeIfAbsent(shopId, k -> {
            TransactionSummary s = new TransactionSummary();
            s.setShopId(shopId);
            s.setBillDate(targetDate);
            s.setTotalIncome(0L);
            s.setTotalRefund(0L);
            s.setFeeAmount(0L);
            s.setFeeReturnAmount(0L);
            s.setIncomeCount(0);
            s.setRefundCount(0);
            s.setTotalCount(0);
            return s;
        });

        summary.setTotalCount(summary.getTotalCount() + 1);
        if (isIncome) {
            summary.setTotalIncome(summary.getTotalIncome() + income);
            summary.setIncomeCount(summary.getIncomeCount() + 1);
            summary.setFeeAmount(summary.getFeeAmount() + (feeAmount != null ? feeAmount : 0));
        } else {
            summary.setTotalRefund(summary.getTotalRefund() + refund);
            summary.setRefundCount(summary.getRefundCount() + 1);
            summary.setFeeReturnAmount(summary.getFeeReturnAmount() + (feeReturnAmount != null ? feeReturnAmount : 0));
        }
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void subLedgerReconciliationTask() {
        subLedgerReconciliationTask(LocalDate.now().minusDays(1));
    }

    public void subLedgerReconciliationTask(LocalDate targetDate) {
        String billDate = targetDate.toString();
        log.info("开始执行分账对账统计任务，日期：{}", billDate);

        flowMapper.deleteSubLedgerSummaryByDate(billDate);

        TransactionFlowSearchParam param = new TransactionFlowSearchParam();
        param.setStartTime(targetDate.atStartOfDay());
        param.setEndTime(targetDate.atTime(LocalTime.MAX));

        List<SubLedgerSummary> summaries = flowMapper.selectSubLedgerSummaryGrouped(param);
        if (summaries != null && !summaries.isEmpty()) {
            for (SubLedgerSummary summary : summaries) {
                summary.setBillDate(targetDate);
                flowMapper.insertSubLedgerSummary(summary);
            }
        }
        log.info("分账对账统计任务执行完成，共处理 {} 个店铺的汇总数据", summaries != null ? summaries.size() : 0);
    }
}