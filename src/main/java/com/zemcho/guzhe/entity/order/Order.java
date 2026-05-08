package com.zemcho.guzhe.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @title: Order
 * @Description:
 * @Date: 2026/4/27 20:09
 */
@Data
public class Order {
    // id
    private Integer id;

    // 下单用户id
    private Integer userId;

    // 下单手机号
    private String phone;

    // 下单用户昵称
    private String nickName;

    // 商家id
    private Integer shopId;

    // 订单类型：1商品订单
    private Integer orderType;

    // 对应类型的id
    private Integer orderId;

    // 订单编号
    private String orderNo;

    // 通莞支付订单号
    private String upOrderNo;

    // 单价（分）
    private Integer price;

    // 数量
    private Integer num;

    // 支付总金额（分）
    private Integer amount;

    // 状态: 1已支付、2已退款
    private Integer status;

    // 支付时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    // 退款金额(分)
    private Integer refundAmount;

    // 退款时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refundTime;

    // 分账状态：0未分账，1已分账
    private Integer divideStatus;

    // 分账金额（分）
    private Integer divideAmount;

    // 分账时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime divideTime;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
