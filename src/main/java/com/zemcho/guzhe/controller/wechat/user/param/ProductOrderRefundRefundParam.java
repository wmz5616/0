package com.zemcho.guzhe.controller.wechat.user.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * @title: ProductOrderRefundRefundParam
 * @Description:
 * @Date: 2026/4/30 13:55
 */
@Data
public class ProductOrderRefundRefundParam {
    @NotNull(message = "订单id为空")
    private Integer orderId;

    @NotBlank(message = "原因为空")
    private String refundReason;

    private List<String> images;
}
