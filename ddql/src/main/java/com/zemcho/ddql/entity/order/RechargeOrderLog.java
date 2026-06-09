package com.zemcho.ddql.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @title: RechargeOrderLog
 * @Description:
 * @Date: 2025/10/10 19:02
 */
@Data
public class RechargeOrderLog {
    // 主键id
    private Integer id;

    // 订单id
    private Integer orderId;

    // 订单编号
    private String orderNo;

    // 操作人id
    private Integer userId;

    // 操作人名称
    private String userName;

    // 操作
    private String handle;

    // 详情
    private String details;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
