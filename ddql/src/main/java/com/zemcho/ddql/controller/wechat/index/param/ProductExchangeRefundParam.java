package com.zemcho.ddql.controller.wechat.index.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * @title: ProductExchangeRefundParam
 * @Description:
 * @Date: 2025/10/13 17:35
 */
@Data
public class ProductExchangeRefundParam {
    @NotNull(message = "订单id为空")
    private Integer orderId;

    @NotBlank(message = "原因为空")
    private String refundReason;

    private List<String> images;
}
