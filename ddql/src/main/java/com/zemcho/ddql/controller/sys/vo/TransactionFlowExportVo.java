package com.zemcho.ddql.controller.sys.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 交易流水导出对象 (对应图片 3)
 */
@Data
public class TransactionFlowExportVo {

    @ColumnWidth(25)
    @ExcelProperty("交易时间")
    private String transactionTime;

    @ColumnWidth(25)
    @ExcelProperty("交易订单号")
    private String orderNo;

    @ColumnWidth(10)
    @ExcelProperty("类型")
    private String type;

    @ColumnWidth(15)
    @ExcelProperty("手续费（元）")
    private BigDecimal serviceFee;

    @ColumnWidth(15)
    @ExcelProperty("收入（元）")
    private BigDecimal income;

    @ColumnWidth(15)
    @ExcelProperty("支出（元）")
    private BigDecimal expense;

    @ColumnWidth(30)
    @ExcelProperty("备注")
    private String remark;

    @ColumnWidth(25)
    @ExcelProperty("商户订单号")
    private String merchantOrderNo;
}
