package com.zemcho.guzhe.controller.wechat.screenOrder.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 店位订单详情参数
 */
@Data
public class ScreenOrderInfoParam {
    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
}
