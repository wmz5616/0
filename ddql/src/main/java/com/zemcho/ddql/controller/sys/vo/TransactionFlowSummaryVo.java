package com.zemcho.ddql.controller.sys.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionFlowSummaryVo {

    private String merchantNo; // 对账单所属商户编号

    private Long totalCount = 0L; // 交易总笔数

    private BigDecimal incomeAmount = BigDecimal.ZERO; // 交易总金额 (收入)

    private BigDecimal totalServiceFee = BigDecimal.ZERO; // 交易手续费金额

    private Long refundCount = 0L; // 退款总笔数

    private BigDecimal refundAmount = BigDecimal.ZERO; // 退款总金额

    private BigDecimal refundFeeReturn = BigDecimal.ZERO; // 退款退回手续费金额

    private Long incomeCount = 0L;

    private Long expenseCount = 0L;

    private BigDecimal expenseAmount = BigDecimal.ZERO;

}
