package com.zemcho.guzhe.controller.wechat.user.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @title: ProductOrderUpdateBaseParam
 * @Description:
 * @Date: 2026/4/28 9:06
 */
@Data
public class ProductOrderUpdateBaseParam {
    @NotNull(message = "订单id为空")
    private Integer orderId;

    private Integer addressId;

    private String remark;
}
