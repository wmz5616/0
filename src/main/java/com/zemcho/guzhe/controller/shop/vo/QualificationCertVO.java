package com.zemcho.guzhe.controller.shop.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QualificationCertVO {
    
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 营业执照图片URL
     */
    private String businessLicense;

    /**
     * 其他附件URL（JSON格式）
     */
    private String otherFile;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 联系邮箱
     */
    private String email;

    /**
     * 申请时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime submitTime;

    /**
     * 认证结果 0未审核 1通过 2驳回
     */
    private Integer certResult;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 审核人ID
     */
    private Integer certUser;

    /**
     * 申请人ID
     */
    private Integer userId;

    /**
     * 填写端 1后台 2小程序
     */
    private Integer certSide;

    /**
     * 对应的商家ID
     */
    private Integer shopId;

    /**
     * 商家审核记录ID
     */
    private Integer shopAuditId;

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

    /**
     * 商家名称（来自 shop_audit 表）
     */
    private String shopName;
}