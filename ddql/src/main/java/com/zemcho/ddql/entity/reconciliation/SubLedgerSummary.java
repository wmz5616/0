package com.zemcho.ddql.entity.reconciliation;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SubLedgerSummary {
    private Integer id;
    private Integer shopId; // 店铺ID
    private LocalDate billDate;
    private Long totalDivideAmount; // 分账总金额(分)
    private Long totalHandlingCharge; // 通莞手续费总额(分)
    private Long totalPlatformCharge; // 平台收费总额(分)
    private Integer totalCount;
    private LocalDateTime createTime;
}
