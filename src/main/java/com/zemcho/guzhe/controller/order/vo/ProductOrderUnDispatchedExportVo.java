package com.zemcho.guzhe.controller.order.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.zemcho.guzhe.util.excel.converter.common.AmountConverter;
import lombok.Data;

/**
 * @title: ProductOrderUnDispatchedExportVo
 * @Description:
 * @Date: 2026/4/30 16:36
 */
@Data
public class ProductOrderUnDispatchedExportVo {
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
     * 支付总金额（分）
     */
    @ExcelProperty(value = "订单金额(元)", converter = AmountConverter.class)
    @ColumnWidth(20)
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
