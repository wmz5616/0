package com.zemcho.guzhe.controller.product.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author HXH
 */
@Data
public class StockParam {
    @NotNull(message = "商品id不能为空")
    private Integer productId;

    @NotNull(message = "库存不能为空")
    private Integer stock;
}
