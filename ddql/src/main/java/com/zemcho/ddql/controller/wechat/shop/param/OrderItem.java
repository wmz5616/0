package com.zemcho.ddql.controller.wechat.shop.param;

import lombok.Data;
//扫码购买商品明细表
@Data
public class OrderItem {
        // 商品ID
        private Integer productId;
         // 商品名称
        private String itemName;
        // 规格名称
        private String specName;
        // 单位（如：个、斤、件）
        private String unit;
        // 单价（单位：分）
        private Integer unitPrice;
        // 数量
        private Integer quantity;
        // 行总金额（单位：分）
        private Integer totalAmount;
        // 该行抵扣金币
        private Integer deductCoin;
        // 该行抵扣金额
        private Integer deductAmount;
        // 该行实付金额
        private Integer payAmount;

    }