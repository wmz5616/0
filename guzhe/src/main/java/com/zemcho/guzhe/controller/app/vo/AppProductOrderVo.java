package com.zemcho.guzhe.controller.app.vo;

import lombok.Data;

/**
 * @title: AppProductOrderVo
 * @Description:
 * @Date: 2026/5/7 16:02
 */
@Data
public class AppProductOrderVo {
    // 订单id
    private Integer id;

    // 订单编号
    private String orderNo;

    // 下单用户id
    private Integer userId;

    // 下单用户手机号
    private String phone;

    // 下单用户昵称
    private String nickName;

    // 商品id
    private Integer productId;

    // 商品编号
    private String productNo;

    // 商品名称
    private String productName;

    // 支付总金额（分）
    private Integer amount;

    // 订单状态: 0待支付、1待使用(虚拟商品)、2待发货(非虚拟商品)、3已发货(非虚拟商品)、4已完成、5退款中、6已退款、7已过期(虚拟商品)、8已取消
    private Integer status;
}
