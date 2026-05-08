package com.zemcho.guzhe.controller.wechat.shop.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.guzhe.entity.shop.QualificationCert;
import com.zemcho.guzhe.entity.shop.ShopAuditIndustry;
import com.zemcho.guzhe.entity.shop.ShopManager;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author HXH
 */
@Data
public class ShopAuditDetailVO {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 申请用户ID
     */
    private Integer userId;

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
     * 提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime submitTime;

    /**
     * 审核状态 0-待审核 1-已通过 2-已驳回
     */
    private Integer auditStatus;

    /**
     * 审核人ID
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
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 审核通过后生成的店铺ID
     */
    private Integer shopId;

    /**
     * 行业类别列表（关联查询）
     */
    private List<ShopAuditIndustry> industryCategories;

    /**
     * 商家管理人员列表（关联查询）
     */
    private List<ShopManager> managers;

    /**
     * 所属商超id
     */
    private List<Integer> circleIds;

    /**
     * 资质认证信息（如果有则返回）
     */
    private QualificationCert qualificationCert;

    /**
     * 店铺启用状态 0禁用 1启用
     */
    private Integer status;
    /**
     * 推荐排序
     */
    private Integer recommendOrder;
}
