package com.zemcho.guzhe.controller.wechat.user.param;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @title: ProductOrderAddParam
 * @Description:
 * @Date: 2026/4/28 9:06
 */
@Data
public class ProductOrderAddParam {
    @NotNull(message = "商品id为空")
    private Integer productId;

    @NotNull(message = "数量为空")
    @Min(value = 1, message = "数量不能小于1")
    private Integer num;

    private Integer equipmentId = 0;

    private Integer addressId = 0;

    private String remark = "";
}
