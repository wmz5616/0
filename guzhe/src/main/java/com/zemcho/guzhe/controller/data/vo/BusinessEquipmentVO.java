package com.zemcho.guzhe.controller.data.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
//商家设备统计
@Data
public class BusinessEquipmentVO {
    // 设备ID
    @ExcelIgnore
    private Integer equipmentId;

    // 备注
    @ExcelProperty(value = "备注")
    @ColumnWidth(20)
    private String remark;

    // 所属商超
    @ExcelProperty(value = "所属商超")
    @ColumnWidth(20)
    private String supermarketName;

    // 设备编号
    @ExcelProperty(value = "终端设备")
    @ColumnWidth(20)
    private String serialNumber;

    // 订单数
    @ExcelProperty(value = "订单数")
    @ColumnWidth(15)
    private Integer orderNum;

    // 待发货订单数
    @ExcelProperty(value = "待发货订单数")
    @ColumnWidth(15)
    private Integer pendingDeliveryNum;

    // 已完成订单数
    @ExcelProperty(value = "已完成订单数")
    @ColumnWidth(15)
    private Integer completedNum;

    // 订单收入（分）
    @ExcelProperty(value = "订单收入")
    @ColumnWidth(15)
    private Integer orderIncome;
}