package com.zemcho.ddql.entity.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用币规则实体类
 */
@Data
public class CoinRule {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 起始金额 （元）
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
     * 对应的商家
     */
    private Integer shopId;

    /**
     * 抵扣规则说明
     */
    private String remark;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}