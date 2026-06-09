package com.zemcho.ddql.controller.sys.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 交易汇总显示对象
 */
@Data
public class TransactionSummaryVo {

    /** 账单日期 */
    @ExcelProperty({"交易汇总", "账单日期"})
    private String billDate;

    /** 交易收入（元） */
    @ExcelProperty({"交易汇总", "交易收入（元）"})
    private BigDecimal transactionIncome = BigDecimal.ZERO;

    /** 退款支出（元） */
    @ExcelProperty({"交易汇总", "退款支出（元）"})
    private BigDecimal refundExpense = BigDecimal.ZERO;

    /** 交易手续费支出（元） */
    @ExcelProperty({"交易汇总", "交易手续费支出（元）"})
    private BigDecimal transactionFeeExpense = BigDecimal.ZERO;

    /** 交易手续费退回（元） */
    @ExcelProperty({"交易汇总", "交易手续费退回（元）"})
    private BigDecimal transactionFeeReturn = BigDecimal.ZERO;
}
