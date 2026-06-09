package com.zemcho.ddql.controller.audit.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SettlementApplicationVO {

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime auditTime;

    /**
     * 审核人电话
     */
    private String auditPhone;

    /**
     * 店铺id
     */
    private Integer shopId;

    private String name;

    private String coverImageUrl;

    private String userName;

    /**
     * 资质认证状态
     */
    private Integer qualificationCert;
    // 删除时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deleteTime;
}
