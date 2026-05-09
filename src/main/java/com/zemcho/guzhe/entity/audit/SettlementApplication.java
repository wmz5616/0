package com.zemcho.guzhe.entity.audit;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 入驻申请实体类
 */
@Data
public class SettlementApplication {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 申请用户id
     */
    private Integer userId;

    /**
     * 申请用户电话
     */
    private String phone;

    /**
     * 申请时间
     */
    private LocalDateTime submitTime;

    /**
     * 申请状态 0待审核 1已通过 2已驳回
     */
    private Integer applyResult;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 审核人id
     */
    private Integer auditUser;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核人电话
     */
    private String auditPhone;

    /**
     * 店铺id
     */
    private Integer shopId;

    /**
     * 店铺入驻审核id
     */
    private Integer shopAuditId;
}