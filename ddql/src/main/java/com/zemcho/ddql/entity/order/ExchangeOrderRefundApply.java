package com.zemcho.ddql.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @title: ExchangeOrderRefundApply
 * @Description:
 * @Date: 2025/10/10 19:02
 */
@Data
public class ExchangeOrderRefundApply {
    // 主键id
    private Integer id;

    // 订单id
    private Integer orderId;

    // 退货申请原因
    private String reason;

    // 退货申请图片，多个用英文逗号隔开
    private String images;

    // 状态: 1审核中、2审核通过、3审核驳回
    private Integer status;

    // 审核管理员id
    private Integer adminId;

    // 审核管理员账号
    private String account;

    // 审核管理员姓名
    private String name;

    // 退货金额(金币)
    private Integer refundAmount;

    // 审核时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;

    // 审核说明
    private String auditRemark;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
