package com.zemcho.guzhe.controller.sys.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionFlowSummaryVo {

    private Long totalCount = 0L;

    private Long incomeCount = 0L;

    private BigDecimal incomeAmount = BigDecimal.ZERO;

    private Long expenseCount = 0L;

    private BigDecimal expenseAmount = BigDecimal.ZERO;

    private BigDecimal totalServiceFee = BigDecimal.ZERO;

}
