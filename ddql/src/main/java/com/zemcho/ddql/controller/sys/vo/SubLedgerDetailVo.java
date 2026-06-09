package com.zemcho.ddql.controller.sys.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 分账明细显示对象
 */
@Data
public class SubLedgerDetailVo {

    /** 分账时间 */
    @ExcelProperty("分账时间")
    private String transactionTime;

    /** 分账金额（元） */
    @ExcelProperty("分账金额（元）")
    private BigDecimal divideAmount = BigDecimal.ZERO;

    /** 通莞手续费（元） */
    @ExcelProperty("通莞手续费（元）")
    private BigDecimal tongGuanFee = BigDecimal.ZERO;

    /** 平台收费（元） */
    @ExcelProperty("平台收费（元）")
    private BigDecimal platformFee = BigDecimal.ZERO;

    /** 订单金额（元） */
    @ExcelProperty("订单金额（元）")
    private BigDecimal orderAmount = BigDecimal.ZERO;

    /** 订单号 */
    @ExcelProperty("订单号")
    private String orderNo;

    /** 分账请求号 */
    @ExcelProperty("分账请求号")
    private String subLedgerRequestNo;
}
