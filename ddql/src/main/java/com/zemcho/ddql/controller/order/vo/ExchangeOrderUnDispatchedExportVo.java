package com.zemcho.ddql.controller.order.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * @title: orderUnDispatchedExportVo
 * @Description:
 * @Date: 2025/10/23 19:02
 */
@Data
public class ExchangeOrderUnDispatchedExportVo {
    /**
     * 订单编号
     */
    @ExcelProperty(value = "订单编号")
    @ColumnWidth(25)
    private String orderNo;

    /**
     * 商品编号
     */
    @ExcelProperty(value = "商品编号")
    @ColumnWidth(25)
    private String productNo;

    /**
     * 商品名称
     */
    @ExcelProperty(value = "商品名称")
    @ColumnWidth(25)
    private String productName;

    /**
     * 支付总金额（金币）
     */
    @ExcelProperty(value = "订单总金额/金币")
    @ColumnWidth(30)
    private Integer amount;

    /**
     * 快递公司名称
     */
    @ExcelProperty(value = "快递公司名称")
    @ColumnWidth(30)
    private String expressCompanyName = "";

    /**
     * 快递单号
     */
    @ExcelProperty(value = "快递单号")
    @ColumnWidth(30)
    private String expressNo = "";
}
