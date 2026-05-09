package com.zemcho.guzhe.controller.app.param;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @title: ProductOrderQRParam
 * @Description:
 * @Date: 2026/5/7 15:12
 */
@Data
public class ProductOrderQRParam {
    @NotNull(message = "商品id为空")
    private Integer productId;

    @NotNull(message = "数量为空")
    @Min(value = 1, message = "数量不能小于1")
    private Integer num;
}
