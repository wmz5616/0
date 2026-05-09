package com.zemcho.guzhe.controller.sys.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth; // 导入此包
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionFlowVo {

    @ColumnWidth(35)
    @ExcelProperty("交易时间")
    private String transactionTime;

    @ColumnWidth(20)
    @ExcelProperty("交易订单号")
    private String orderNo;

    @ExcelProperty("类型")
    private String type;

    @ExcelProperty("手续费（元）")
    private BigDecimal serviceFee;

    @ExcelProperty("收入（元）")
    private BigDecimal income;

    @ExcelProperty("支出（元）")
    private BigDecimal expense;

    @ColumnWidth(30)
    @ExcelProperty("备注")
    private String remark;

    @ColumnWidth(25)
    @ExcelProperty("商户订单号")
    private String merchantOrderNo;
}
