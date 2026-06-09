package com.zemcho.ddql.entity.reconciliation;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TransactionSummary {
    private Integer id;
    private Integer shopId; // 店铺ID
    private LocalDate billDate;
    private Long totalIncome; // 交易收入总额(分)
    private Long totalRefund; // 退款支出总额(分)
    private Long feeAmount; // 手续费支出总额(分)
    private Long feeReturnAmount; // 手续费退回总额(分)
    private Integer incomeCount;
    private Integer refundCount;
    private Integer totalCount;
    private LocalDateTime createTime;
}
