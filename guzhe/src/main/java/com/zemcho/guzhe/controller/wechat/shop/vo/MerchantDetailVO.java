package com.zemcho.guzhe.controller.wechat.shop.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 小程序商家管理详情 VO
 */
@Data
public class MerchantDetailVO {
    /**
     * 商家 ID
     */
    private Integer shopId;
    
    /**
     * 商家名称
     */
    private String shopName;
    
    /**
     * 商家 Logo
     */
    private String coverImageUrl;
    
    /**
     * 认证状态 0-未认证 1-待审核 2-已通过 3-已驳回
     */
    private Integer qualificationCert;
    
    /**
     * 统计开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 统计结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 营业额（统计时间段内实付金额）
     */
    private BigDecimal revenue;
    
    /**
     * 订单数（统计时间段内订单数量）
     */
    private Integer orderCount;
    
    /**
     * 待结算金额（该商户未结算的账户余额）
     */
    private BigDecimal pendingAmount;
}
