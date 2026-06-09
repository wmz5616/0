package com.zemcho.ddql.controller.sys.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 分账汇总显示对象
 */
@Data
public class SubLedgerSummaryVo {

    /** 账单日期 */
    @ExcelProperty({"分账汇总", "账单日期"})
    private String billDate;

    /** 分账金额 (元) */
    @ExcelProperty({"分账汇总", "分账金额 (元)"})
    private BigDecimal divideAmount = BigDecimal.ZERO;

    /** 分账退款金额（元） */
    @ExcelProperty({"分账汇总", "分账退款金额（元）"})
    private BigDecimal refundAmount = BigDecimal.ZERO;
}
