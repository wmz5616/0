package com.zemcho.ddql.controller.order.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @title: OrderRefundParam
 * @Description:
 * @Date: 2025/10/14 15:14
 */
@Data
public class OrderRefundParam {
    @NotNull(message = "订单id为空")
    private Integer orderId;

    @NotNull(message = "退货币额为空")
    private Integer refundAmount;

    @NotBlank(message = "退货原因为空")
    private String refundReason;
}
