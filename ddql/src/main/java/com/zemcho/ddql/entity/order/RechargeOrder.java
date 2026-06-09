package com.zemcho.ddql.entity.order;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.ddql.util.excel.converter.common.AmountConverter;
import com.zemcho.ddql.util.excel.converter.order.RechargeOrderStatusConverter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @title: RechargeOrder
 * @Description:
 * @Date: 2025/10/10 19:02
 */
@Data
public class RechargeOrder {
    // 订单id
    @ExcelProperty(value = "订单id")
    @ColumnWidth(15)
    private Integer id;

    // 订单编号
    @ExcelProperty(value = "订单编号")
    @ColumnWidth(25)
    private String orderNo;

    // 微信交易订单号
    @ExcelProperty(value = "微信交易订单号")
    @ColumnWidth(25)
    private String wxTransactionNo;

    // 下单用户id
    @ExcelIgnore
    private Integer userId;

    // 下单手机号
    @ExcelProperty(value = "下单手机号")
    @ColumnWidth(25)
    private String phone;

    // 下单用户昵称
    @ExcelProperty(value = "下单用户昵称")
    @ColumnWidth(25)
    private String nickName;

    //团队id
    @ExcelIgnore
    private Integer teamId;

    // 团队名字
    @ExcelProperty(value = "充值团体")
    @ColumnWidth(25)
    private String teamName;

    // 团队类型 0企事单位 1政府部分 2家庭 3朋友
    @ExcelIgnore
    private Integer teamType;

    // 充值活动id
    @ExcelIgnore
    private Integer actId;

    // 支付总金额（充值金额） 单位为分
    @ExcelProperty(value = "充值金额", converter = AmountConverter.class)
    @ColumnWidth(20)
    private Integer amount;

    // 赠送金额 单位为分
    @ExcelProperty(value = "赠送金额", converter = AmountConverter.class)
    @ColumnWidth(20)
    private Integer giveAmount;

    // 支付方式 1微信支付
    @ExcelIgnore
    private Integer payType;

    // 订单状态: 0无、1待支付、2已支付、3已取消、4已退款
    @ExcelProperty(value = "充值状态", converter = RechargeOrderStatusConverter.class)
    @ColumnWidth(20)
    private Integer status;

    // 支付时间
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    // 退款订单号
    @ExcelIgnore
    private String refundNo;

    // 退款金额，单位为分
    @ExcelProperty(value = "退款金额", converter = AmountConverter.class)
    @ColumnWidth(20)
    private Integer refundAmount;

    // 退款时间
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refundTime;

    // 退款说明
    @ExcelIgnore
    private String refundRemark;

    // 创建时间
    @ExcelProperty(value = "充值时间")
    @ColumnWidth(20)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
