package com.zemcho.guzhe.controller.order.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @title: OrderRefundParam
 * @Description:
 * @Date: 2026/05/03 15:16
 */
@Data
public class OrderRefundParam {
    @NotNull(message = "订单id为空")
    private Integer orderId;

    @NotNull(message = "退款金额为空")
    private Integer refundAmount;

    @NotBlank(message = "退款原因为空")
    private String refundReason;
}
