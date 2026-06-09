package com.zemcho.ddql.controller.recharge.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 充值活动实体类
 *
 * @author Ryan
 */
@Data
public class RechargeActivityParam {

    // 主键
    private Integer id = 0;

    // 充值金额 单位为分
    @NotNull(message = "充值金额不能为空")
    private Integer rechargeAmount;

    // 是否开启赠送金额 0关闭 1开启
    @NotNull(message = "是否开启赠送金额不能为空")
    private Integer enableGift;

    // 赠送金额 单位为分
    private Integer giftAmount = 0;

    // 活动充值次数限制，0为不限制
    private Integer rechargeCount = 0;

    // 排序
    private Integer sort;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
