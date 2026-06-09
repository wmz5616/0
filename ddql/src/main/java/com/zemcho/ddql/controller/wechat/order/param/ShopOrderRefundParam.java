package com.zemcho.ddql.controller.wechat.order.param;

import jakarta.validation.constraints.Min;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
/**
 * @author HXH
 */
@Data
public class ShopOrderRefundParam {
    @NotNull(message = "订单ID不能为空")
    private Integer orderId;

    @Min(value = 0, message = "退款金额不能为负数")
    private Integer refundAmount;

    @Min(value = 0, message = "退回币额不能为负数")
    private Integer refundCoin;

    @NotBlank(message = "退款原因不能为空")
    private String refundReason;
}
