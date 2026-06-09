package com.zemcho.ddql.controller.wechat.index.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 商品订单支付配置请求参数
 */
@Data
public class ProductExchangeOrderPayParam {
    /**
     * 订单ID
     */
    @NotNull(message = "订单id为空")
    private Integer orderId;
}
