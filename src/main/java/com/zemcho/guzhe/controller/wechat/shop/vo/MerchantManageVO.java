package com.zemcho.guzhe.controller.wechat.shop.vo;

import lombok.Data;

/**
 * 小程序商家管理列表 VO
 */
@Data
public class MerchantManageVO {
    /**
     * 商家 ID
     */
    private Integer shopId;
    
    /**
     * 商家名称
     */
    private String shopName;
    
    /**
     * 认证状态 0-未认证 1-待审核 2-已通过 3-已驳回
     */
    private Integer qualificationCert;
    
    /**
     * 商家状态 0-正常 1-禁用 2-已注销
     */
    private Integer shopStatus;
    
    /**
     * 启用状态 0-禁用 1-启用
     */
    private Integer status;
}
