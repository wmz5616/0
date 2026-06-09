package com.zemcho.guzhe.controller.sys.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionFlowVo {

    @ExcelIgnore
    private Integer id;

    @ColumnWidth(25)
    @ExcelProperty("时间")
    private String transactionTime;

    @ColumnWidth(25)
    @ExcelProperty("订单号")
    private String orderNo;

    @ColumnWidth(30)
    @ExcelProperty("商品")
    private String productName;

    @ColumnWidth(25)
    @ExcelProperty("规格")
    private String specification;

    @ColumnWidth(10)
    @ExcelProperty("总数量")
    private Integer totalQuantity;

    @ColumnWidth(10)
    @ExcelProperty("类型")
    private String type;

    @ColumnWidth(15)
    @ExcelProperty("金额")
    private BigDecimal amount;

    @ColumnWidth(15)
    @ExcelProperty("手续费")
    private BigDecimal serviceFee;

    @ColumnWidth(20)
    @ExcelProperty("下单人")
    private String orderUser;

    @ExcelIgnore
    private String orderUserPhone;

    @ColumnWidth(30)
    @ExcelProperty("备注")
    private String remark;

    @ColumnWidth(25)
    @ExcelProperty("商户请求号")
    private String merchantOrderNo;

    @ColumnWidth(30)
    @ExcelProperty("易宝订单号")
    private String yibaoOrderNo;

    @ColumnWidth(25)
    @ExcelProperty("原收款订单商户请求号")
    private String originMerchantOrderNo;

    @ColumnWidth(30)
    @ExcelProperty("原收款订单易宝订单号")
    private String originOrderno;

    @ExcelIgnore
    private BigDecimal income;

    @ExcelIgnore
    private BigDecimal expense;
}
