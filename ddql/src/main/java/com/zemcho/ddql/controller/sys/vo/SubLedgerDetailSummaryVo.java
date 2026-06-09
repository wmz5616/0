package com.zemcho.ddql.controller.sys.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SubLedgerDetailSummaryVo {
    private Long totalCount = 0L;
    private BigDecimal totalDivideAmount = BigDecimal.ZERO;
    private BigDecimal totalTongGuanFee = BigDecimal.ZERO;
    private BigDecimal totalPlatformFee = BigDecimal.ZERO;
}
