package com.zemcho.ddql.entity.order;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zemcho.ddql.util.excel.converter.order.ExchangeOrderExpressStatusConverter;
import com.zemcho.ddql.util.excel.converter.order.ExchangeOrderStatusConverter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @title: ExchangeOrder
 * @Description:
 * @Date: 2025/10/13 17:07
 */
@Data
public class ExchangeOrder {
    /**
     * 订单id
     */
    @ExcelProperty(value = "订单id")
    @ColumnWidth(15)
    private Integer id;

    /**
     * 订单编号
     */
    @ExcelProperty(value = "订单编号")
    @ColumnWidth(25)
    private String orderNo;

    /**
     * 下单用户id
     */
    @ExcelIgnore
    private Integer userId;

    /**
     * 下单手机号
     */
    @ExcelProperty(value = "下单手机号")
    @ColumnWidth(25)
    private String phone;

    /**
     * 下单用户昵称
     */
    @ExcelProperty(value = "下单用户昵称")
    @ColumnWidth(25)
    private String nickName;

    /**
     * 商品id
     */
    @ExcelIgnore
    private Integer productId;

    /**
     * 商品编号
     */
    @ExcelProperty(value = "商品编号")
    @ColumnWidth(25)
    private String productNo;

    /**
     * 商品封面图
     */
    @ExcelIgnore
    private String coverImage;

    /**
     * 商品名称
     */
    @ExcelProperty(value = "商品名称")
    @ColumnWidth(25)
    private String productName;

    /**
     * 规格
     */
    @ExcelProperty(value = "规格")
    @ColumnWidth(15)
    private String specification;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位")
    @ColumnWidth(15)
    private String unit;

    /**
     * 是否是虚拟商品（0:否, 1:是）
     */
    @ExcelIgnore
    private Integer isVirtual;

    /**
     * 有效截止时间，虚拟商品才有
     */
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;

    /**
     * 单价（金币）
     */
    @ExcelIgnore
    private Integer singleAmount;

    /**
     * 兑换数量
     */
    @ExcelProperty(value = "总数量")
    @ColumnWidth(10)
    private Integer num;

    /**
     * 支付总金额（金币）
     */
    @ExcelProperty(value = "订单金额")
    @ColumnWidth(20)
    private Integer amount;

    /**
     * 支付方式 0金币 1组合 2现金
     */
    @ExcelIgnore
    private Integer payWay;

    /**
     * 支付总金币
     */
    @ExcelProperty(value = "备注")
    @ColumnWidth(20)
    private String remark;

    /**
     * 现金支付金额（单位：分）
     */
    @ExcelIgnore
    private Integer cashAmount;

    /**
     * 通莞支付订单号
     */
    @ExcelIgnore
    private String upOrderId;

    /**
     * 订单状态: 0待支付、1待使用(虚拟商品)、2待发货(非虚拟商品)、3已发货(非虚拟商品)、4已完成、5退款中、6已退款、7已过期(虚拟商品)、8已取消
     */
    @ExcelProperty(value = "订单状态", converter = ExchangeOrderStatusConverter.class)
    @ColumnWidth(20)
    private Integer status;

    /**
     * 快递公司名称
     */
    @ExcelIgnore
    private String expressCompanyName;

    /**
     * 快递公司标识码
     */
    @ExcelIgnore
    private String expressCompanyCode;

    /**
     * 快递单号
     */
    @ExcelIgnore
    private String expressNo;

    /**
     * 物流状态：-2--无，-1--待发货，0--在途，1--揽件，2--疑难，3--签收，4--退签，5--派件，6--退回，10--待清关，11--清关中，12--已清关，
     * 13--清关异常，14--收件人拒签
     */
    @ExcelProperty(value = "到货状态", converter = ExchangeOrderExpressStatusConverter.class)
    @ColumnWidth(20)
    private Integer expressStatus;

    /**
     * 退货金额(金币)
     */
    @ExcelIgnore
    private Integer refundAmount;

    /**
     * 现金退款金额（单位：分）
     */
    @ExcelIgnore
    private Integer refundCashAmount;

    /**
     * 退货时间
     */
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime refundTime;

    /**
     * 退货说明
     */
    @ExcelIgnore
    private String refundRemark;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "下单时间")
    @ColumnWidth(20)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 支付成功时间
     */
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    /**
     * 分账状态：0未分账，1已分账
     */
    @ExcelIgnore
    private Integer divideStatus;

    /**
     * 分账金额，单位：分
     */
    @ExcelIgnore
    private Integer divideAmount;

    /**
     * 分账时间
     */
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime divideTime;

    /**
     * 兼容组合支付场景，明确返回支付金币数。
     */
    @JsonProperty("goldAmount")
    public Integer getGoldAmount() {
        return amount;
    }
}
