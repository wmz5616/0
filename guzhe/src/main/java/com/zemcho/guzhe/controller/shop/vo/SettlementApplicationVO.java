package com.zemcho.guzhe.controller.shop.vo;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime submitTime;

    /**
     * 申请状态 0待审核 1已通过 2已驳回
     */
    private Integer applyResult;

    /**
     * 申请用户名称
     */
    private String userName;

    /**
     * 商家名称
     */
    private String name;
    /**
     * 商家封面图片
     */
    private String coverImageUrl;

    /**
     * 审核人id
     */
    private Integer auditUser;

    /**
     * 审核人电话
     */
    private String auditPhone;

    /**
     * 资质认证状态
     */
    private Integer qualificationCert;
}
