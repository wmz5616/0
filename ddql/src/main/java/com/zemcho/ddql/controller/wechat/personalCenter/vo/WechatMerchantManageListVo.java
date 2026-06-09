package com.zemcho.ddql.controller.wechat.personalCenter.vo;

import lombok.Data;

/**
 * 小程序商家管理列表返回对象
 */
@Data
public class WechatMerchantManageListVo {
    /**
     * 商家ID
     */
    private Integer shopId;

    /**
     * 商家名称
     */
    private String shopName;

    /**
     * 资质认证状态 0未认证 1待审核 2已认证 3已驳回
     */
    private Integer qualificationCert;

    /**
     * 入驻申请状态 0待审核 1已通过 2已驳回
     */
    private Integer applyResult;

    /**
     * 是否可注销 0否 1是
     */
    private Integer canCancel;
}
