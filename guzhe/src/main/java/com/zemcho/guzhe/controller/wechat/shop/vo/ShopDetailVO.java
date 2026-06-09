package com.zemcho.guzhe.controller.wechat.shop.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.guzhe.controller.shop.vo.ShopCircleListVO;
import com.zemcho.guzhe.entity.shop.IndustryCategory;
import com.zemcho.guzhe.entity.shop.ShopAuditIndustry;
import com.zemcho.guzhe.entity.supermarket.SupermarketInfo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 小程序商家详情 VO（正式商家）
 */
@Data
public class ShopDetailVO {
    /**
     * 商家 ID
     */
    private Integer id;

    /**
     * 店铺封面图URL
     */
    private String coverImageUrl;

    /**
     * 店铺轮播图URL列表
     */
    private List<String> galleryImages;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 店铺经纬度
     */
    private String location;

    /**
     * 店铺详细地址
     */
    private String address;

    /**
     * 店铺联系人姓名
     */
    private String userName;

    /**
     * 店铺联系电话
     */
    private String phone;

    /**
     * 营业开始时间
     */
    private String startTime;

    /**
     * 营业结束时间
     */
    private String endTime;

    /**
     * 营业时间
     */
    private String businessTime;

    /**
     * 客服电话
     */
    private String customerPhone;

    /**
     * 客服微信二维码图片URL
     */
    private String customerCodeImg;

    /**
     * 店铺介绍（富文本内容）
     */
    private String description;

    /**
     * 行业类别列表
     */
    private List<IndustryCategory> industryCategories;

    /**
     * 所属商超名称列表
     */
    private List<SupermarketInfo> supermarketInfoList;

    /**
     * 资质认证状态 0-未认证 1-待审核 2-已通过 3-已驳回
     */
    private Integer qualificationCert;

    /**
     * 商家启用状态 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 商家状态 0-正常 1-禁用 2-已注销
     */
    private Integer shopStatus;

    /**
     * 待审核退款申请数量
     */
    private Integer refundPendingCount;

    /**
     * 合同照片
     */
    private String contract;

    // 绑定的商户id
    private Integer merchantId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 收款启用状态 0关闭收款 1开启收款
     */
    private Integer receiptStatus;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    public String getBusinessTime() {
        if (businessTime != null && !businessTime.isEmpty()) {
            return businessTime;
        }
        if ((startTime == null || startTime.isEmpty()) && (endTime == null || endTime.isEmpty())) {
            return null;
        }
        if (startTime == null || startTime.isEmpty()) {
            return endTime;
        }
        if (endTime == null || endTime.isEmpty()) {
            return startTime;
        }
        return startTime + " - " + endTime;
    }
}
