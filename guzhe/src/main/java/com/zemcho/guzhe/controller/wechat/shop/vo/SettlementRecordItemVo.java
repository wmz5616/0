package com.zemcho.guzhe.controller.wechat.shop.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 结算记录项
 */
@Data
public class SettlementRecordItemVo {
    @ExcelProperty("结算日期")
    private String settlementTime;

    @ExcelProperty("结算金额")
    private BigDecimal settlementAmount = BigDecimal.ZERO;

    @ExcelProperty("费率")
    private String rate;

    @ExcelProperty("营业额")
    private BigDecimal turnover = BigDecimal.ZERO;

    @ExcelIgnore
    private BigDecimal platformRate = BigDecimal.ZERO;
}
