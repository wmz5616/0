package com.zemcho.guzhe.entity.shop;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资质认证实体类
 */
@Data
public class QualificationCert {
    // 主键ID
    private Integer id;

    // 营业执照
    private String businessLicense;

    // 其他附件
    private String otherFile;

    // 联系电话
    private String phone;

    // 联系邮箱
    private String email;

    // 申请时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime submitTime;

    // 认证结果 0未审核 1通过 2驳回
    private Integer certResult;

    // 驳回原因
    private String rejectReason;

    // 审核人(id)
    private Integer certUser;

    // 申请人id
    private Integer userId;

    // 填写端 1后台 2小程序
    private Integer certSide;

    // 对应的商家id
    private Integer shopId;

    // 对应的待审核的商家id
    private Integer shopAuditId;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}