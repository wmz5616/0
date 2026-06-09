package com.zemcho.ddql.controller.wechat.shop.param;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ShopOrderCreateParam {
    /**
     * 店铺ID
     */
    @NotNull(message = "店铺ID不能为空")
    private Integer shopId;

    /**
     * 订单总金额（单位：元）
     */
    @NotNull(message = "订单金额不能为空")
    @Min(value = 1, message = "订单金额必须大于0")
    private Integer totalAmount;

    /**
     * 抵扣金币数
     */
    @Min(value = 0, message = "抵扣金币数不能为负数")
    private Integer deductCoin;

    /**
     * 抵扣金额（单位：分）
     */
    @Min(value = 0, message = "抵扣金额不能为负数")
    private Integer deductAmount;

    /**
     * 实付金额（单位：分）
     */
    @Min(value = 0, message = "实付金额不能为负数")
    private Integer payAmount;

}
