package com.zemcho.ddql.controller.product.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockParam {
    @NotNull(message = "商品id不能为空")
    private Integer productId;

    @NotNull(message = "库存不能为空")
    private Integer stock;
}
