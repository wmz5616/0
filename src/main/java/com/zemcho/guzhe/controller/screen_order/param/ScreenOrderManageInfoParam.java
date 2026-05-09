package com.zemcho.guzhe.controller.screen_order.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 后台店位订单详情参数
 */
@Data
public class ScreenOrderManageInfoParam {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
}
