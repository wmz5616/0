package com.zemcho.ddql.controller.wechat.index.param;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @title: ProductExchangeParam
 * @Description:
 * @Date: 2025/10/13 17:35
 */
@Data
public class ProductExchangeParam {
    @NotNull(message = "商品id为空")
    private Integer productId;

    @NotNull(message = "数量为空")
    @Min(value = 1, message = "数量不能小于1")
    private Integer num;

    @NotNull(message = "支付总金币为空")
    private Integer amount;

    private Integer addressId = 0;

    private String remark = "";
}
