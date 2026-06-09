package com.zemcho.guzhe.controller.wechat.screenOrder.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 店位订单修改展示内容参数
 */
@Data
public class ScreenOrderDisplayUpdateParam {
    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 展示内容类型：1-商品 2-海报
     */
    @NotNull(message = "展示内容类型不能为空")
    private Integer displayType;
}
