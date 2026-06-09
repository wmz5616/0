package com.zemcho.guzhe.controller.wechat.shop.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 结算记录汇总
 */
@Data
public class SettlementRecordSummaryVo {
    private Long totalCount = 0L;

    private BigDecimal totalAmount = BigDecimal.ZERO;
}
