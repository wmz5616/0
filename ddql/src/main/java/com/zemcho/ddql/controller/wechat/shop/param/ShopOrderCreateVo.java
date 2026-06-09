package com.zemcho.ddql.controller.wechat.shop.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopOrderCreateVo {
    /**
     * 订单ID
     */
    private Integer orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单总金额（分）
     */
    private Integer totalAmount;

    /**
     * 抵扣金币数
     */
    private Integer deductCoin;

    /**
     * 抵扣金额（分）
     */
    private Integer deductAmount;

    /**
     * 实付金额（分）
     */
    private Integer payAmount;

    /**
     * 支付参数（用于调起微信支付）
     */
//    private Object payParams;

    /**
     * 订单创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
