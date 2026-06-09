package com.zemcho.ddql.controller.wechat.shop.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HXH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopOrderExportVo {
    @ExcelProperty(value = "订单号")
    @ColumnWidth(25)
    private String orderNo;

    @ExcelProperty(value = "支付时间")
    @ColumnWidth(25)
    private String orderTime;

    @ExcelProperty(value = "订单金额")
    @ColumnWidth(15)
    private String totalAmount;

    @ExcelProperty(value = "实付金额")
    @ColumnWidth(15)
    private String payAmount;

    @ExcelProperty(value = "抵扣金币数")
    @ColumnWidth(15)
    private Integer deductCoin;

    @ExcelProperty(value = "类型")
    @ColumnWidth(10)
    private String orderType;

    @ExcelProperty(value = "下单人")
    @ColumnWidth(30)
    private String userInfo;

    @ExcelProperty(value = "退款原因")
    @ColumnWidth(20)
    private String refundReason;

    @ExcelProperty(value = "退款时间")
    @ColumnWidth(25)
    private String refundTime;
}
