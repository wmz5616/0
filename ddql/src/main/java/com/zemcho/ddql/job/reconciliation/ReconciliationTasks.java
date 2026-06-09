package com.zemcho.ddql.job.reconciliation;

import com.zemcho.ddql.entity.personalCenter.ShopOrder;
import com.zemcho.ddql.entity.reconciliation.SubLedgerSummary;
import com.zemcho.ddql.entity.reconciliation.TransactionFlow;
import com.zemcho.ddql.entity.reconciliation.TransactionSummary;
import com.zemcho.ddql.controller.sys.param.TransactionFlowSearchParam;
import com.zemcho.ddql.mapper.order.ShopOrderMapper;
import com.zemcho.ddql.mapper.sys.TransactionFlowMapper;
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
    private ShopOrderMapper shopOrderMapper;

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

        List<ShopOrder> orders = shopOrderMapper.selectOrderListForReconciliation(
                targetDate.atStartOfDay(),
                targetDate.atTime(LocalTime.MAX)
        );

        if (orders == null || orders.isEmpty()) {
            log.info("交易对账统计任务执行完成，日期 {} 无订单数据", billDate);
            return;
        }

        List<TransactionFlow> allFlows = new ArrayList<>();
        Map<Integer, TransactionSummary> summaryMap = new HashMap<>();

        for (ShopOrder order : orders) {
            int shopId = order.getShopId() != null ? order.getShopId() : 0;

            String productName = "门店订单";
            String remark = order.getRemark() != null ? order.getRemark() : "";

            if (order.getOrderTime() != null
                    && order.getOrderTime().toLocalDate().equals(targetDate)) {
                TransactionFlow flow = buildFlow(order, targetDate, 1, productName, remark);
                allFlows.add(flow);
                int incomeAmt = order.getPayAmount() != null ? order.getPayAmount() : 0;
                accumulateSummary(summaryMap, shopId, targetDate, incomeAmt, 0, order.getDivideAmount(), 0, true);
            }

            if (order.getRefundTime() != null
                    && order.getRefundTime().toLocalDate().equals(targetDate)) {
                TransactionFlow flow = buildFlow(order, targetDate, 2, productName, remark);
                allFlows.add(flow);
                int refundAmt = order.getPayAmount() != null ? order.getPayAmount() : 0;
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

    private TransactionFlow buildFlow(ShopOrder order, LocalDate targetDate, int type, String productName, String remark) {
        TransactionFlow flow = new TransactionFlow();
        flow.setShopId(order.getShopId() != null ? order.getShopId() : 0);
        flow.setBillDate(targetDate);
        flow.setTransactionTime(type == 1 ? order.getOrderTime() : order.getRefundTime());
        flow.setOrderNo(order.getOrderNo() != null ? order.getOrderNo() : "");
        flow.setOrderId(order.getId() != null ? order.getId().longValue() : 0L);
        flow.setOrderType(3); // 3表示门店订单
        flow.setUserId(order.getUserId() != null ? order.getUserId().longValue() : 0L);
        flow.setType(type);
        if (type == 1) {
            flow.setAmount(order.getPayAmount() != null ? order.getPayAmount().longValue() : 0L);
        } else {
            flow.setAmount(order.getPayAmount() != null ? order.getPayAmount().longValue() : 0L);
        }
        flow.setServiceFee(order.getDivideAmount() != null ? order.getDivideAmount().longValue() : 0L);
        flow.setMerchantOrderNo("");
        flow.setOrderno("");
        flow.setOriginMerchantOrderNo("");
        flow.setOriginOrderno("");
        flow.setProductName(productName);
        flow.setTotalQuantity(1);
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
