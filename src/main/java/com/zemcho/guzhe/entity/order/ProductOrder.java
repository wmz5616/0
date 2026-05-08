package com.zemcho.guzhe.entity.order;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.guzhe.util.excel.converter.common.AmountConverter;
import com.zemcho.guzhe.util.excel.converter.order.ExpressStatusConverter;
import com.zemcho.guzhe.util.excel.converter.order.ProductOrderStatusConverter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @title: ProductOrder
 * @Description:
 * @Date: 2026/4/27 20:21
 */
@Data
public class ProductOrder {
    // 订单id
    @ExcelProperty(value = "订单id")
    @ColumnWidth(15)
    private Integer id;

    // 订单编号
    @ExcelProperty(value = "订单编号")
    @ColumnWidth(25)
    private String orderNo;

    // 通莞支付订单号
    @ExcelIgnore
    private String upOrderNo;

    // 下单用户id
    @ExcelIgnore
    private Integer userId;

    // 下单用户手机号
    @ExcelProperty(value = "下单手机号")
    @ColumnWidth(25)
    private String phone;

    // 下单用户昵称
    @ExcelProperty(value = "下单用户昵称")
    @ColumnWidth(25)
    private String nickName;

    // 设备id
    @ExcelIgnore
    private Integer equipmentId;

    // 设备编号
    @ExcelProperty(value = "设备编号")
    @ColumnWidth(25)
    private String serialNumber;

    // 商家id
    @ExcelIgnore
    private Integer shopId;

    // 商家名称
    @ExcelProperty(value = "商家")
    @ColumnWidth(25)
    private String shopName;

    // 商品id
    @ExcelIgnore
    private Integer productId;

    // 商品编号
    @ExcelProperty(value = "商品编号")
    @ColumnWidth(25)
    private String productNo;

    // 商品封面图
    @ExcelIgnore
    private String coverImage;

    // 商品名称
    @ExcelProperty(value = "商品名称")
    @ColumnWidth(25)
    private String productName;

    // 规格
    @ExcelProperty(value = "规格")
    @ColumnWidth(15)
    private String specification;

    // 单位
    @ExcelProperty(value = "单位")
    @ColumnWidth(15)
    private String unit;

    // 是否是虚拟商品（0:否, 1:是）
    @ExcelIgnore
    private Integer isVirtual;

    // 有效截止时间，虚拟商品才有
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;

    // 备注
    @ExcelProperty(value = "备注")
    @ColumnWidth(20)
    private String remark;

    // 单价（分）
    @ExcelIgnore
    private Integer price;

    // 数量
    @ExcelProperty(value = "数量")
    @ColumnWidth(15)
    private Integer num;

    // 支付总金额（分）
    @ExcelProperty(value = "订单金额(元)", converter = AmountConverter.class)
    @ColumnWidth(20)
    private Integer amount;

    // 订单状态: 0待支付、1待使用(虚拟商品)、2待发货(非虚拟商品)、3已发货(非虚拟商品)、4已完成、5退款中、6已退款、7已过期(虚拟商品)、8已取消
    @ExcelProperty(value = "订单状态", converter = ProductOrderStatusConverter.class)
    @ColumnWidth(20)
    private Integer status;

    // 支付时间
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    // 退款金额(分)
    @ExcelIgnore
    private Integer refundAmount;

    // 退款时间
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refundTime;

    // 退款说明
    @ExcelIgnore
    private String refundRemark;

    // 快递公司名称
    @ExcelIgnore
    private String expressCompanyName;

    // 快递公司标识码
    @ExcelIgnore
    private String expressCompanyCode;

    // 快递单号
    @ExcelIgnore
    private String expressNo;

    // 物流状态：-2--无，-1--待发货，0--在途，1--揽件，2--疑难，3--签收，4--退签，5--派件，6--退回，10--待清关，11--清关中，12--已清关，
    // 13--清关异常，14--收件人拒签
    @ExcelProperty(value = "物流状态", converter = ExpressStatusConverter.class)
    @ColumnWidth(20)
    private Integer expressStatus;

    // 分账状态：0未分账，1已分账
    @ExcelIgnore
    private Integer divideStatus;

    // 分账金额（分）
    @ExcelIgnore
    private Integer divideAmount;

    // 分账时间
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime divideTime;

    // 创建时间
    @ExcelProperty(value = "下单时间")
    @ColumnWidth(20)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
