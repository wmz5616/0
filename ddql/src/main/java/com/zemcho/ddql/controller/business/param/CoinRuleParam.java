package com.zemcho.ddql.controller.business.param;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用币规则参数类
 */
@Data
public class CoinRuleParam {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 起始金额 （元）
     */
    @NotNull(message = "起始金额不能为空")
    @Min(1)
    private Integer beginAmount;

    /**
     * 满减金额
     */
    @NotNull(message = "满减金额不能为空")
    @Min(1)
    private Integer threshold;

    /**
     * 扣减金币
     */
    @NotNull(message = "扣减金币不能为空")
    @Min(1)
    private Integer deduct;

    /**
     * 最高抵扣金币
     */
    @NotNull(message = "最高抵扣金币不能为空")
    @Min(1)
    private Integer maxDeduct;

    /**
     * 对应的商家
     */
    @NotNull(message = "商家ID不能为空")
    private Integer shopId;

    /**
     * 抵扣规则说明
     */
    private String remark;
}