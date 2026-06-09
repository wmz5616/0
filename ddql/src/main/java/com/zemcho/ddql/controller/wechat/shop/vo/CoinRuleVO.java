package com.zemcho.ddql.controller.wechat.shop.vo;

import lombok.Data;

/**
 * 用币规则VO
 */
@Data
public class CoinRuleVO {

    /**
     * 起始金额（元）
     */
    private Integer beginAmount;

    /**
     * 满减金额
     */
    private Integer threshold;

    /**
     * 扣减金币
     */
    private Integer deduct;

    /**
     * 最高抵扣金币
     */
    private Integer maxDeduct;

    /**
     * 抵扣规则说明
     */
    private String remark;
}
