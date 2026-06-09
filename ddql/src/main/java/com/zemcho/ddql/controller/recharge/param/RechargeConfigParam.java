package com.zemcho.ddql.controller.recharge.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 充值配置实体类
 *
 * @author Ryan
 */
@Data
public class RechargeConfigParam {

    // 主键
    private Integer id;

    // 是否开启充值功能 0关闭 1开启
    @NotNull(message = "是否开启充值功能不能为空")
    private Integer enableRecharge;

    // 是否开启自定义充值金额 0关闭 1开启
    @NotNull(message = "是否开启自定义充值金额不能为空")
    private Integer enableCustomAmount;

    // 最低充值金额 单位为分
    private Integer minAmount;

    // 临界金额 单位为分
    @NotNull(message = "临界金额不能为空")
    private Integer criticalAmount;

    // 充值说明
    private String description;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
