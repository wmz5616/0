package com.zemcho.ddql.controller.audit.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.ddql.entity.business.ShopManager;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShopAuditHandleParam {

    /**
     * 店铺ID
     */
    private Integer id;
    
    /**
     * 店铺封面图URL
     */
    @NotBlank(message = "店铺封面图不能为空")
    private String coverImageUrl;
    
    /**
     * 店铺轮播图URL列表（JSON格式）
     */
    @NotEmpty(message = "店铺轮播图不能为空")
    private List<String> galleryImages;
    
    /**
     * 店铺名称
     */
    @NotBlank(message = "店铺名称不能为空")
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
     * 店铺联系人姓名（店长）
     */
    @NotBlank(message = "店铺联系人姓名不能为空")
    private String userName;
    
    /**
     * 店铺联系电话（店长电话）
     */
    @NotBlank(message = "店铺联系电话不能为空")
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
     * 店铺介绍（富文本内容）
     */
    private String description;
    
    /**
     * 行业类别ID列表
     */
    @NotEmpty(message = "行业类别不能为空")
    private List<Integer> industryCategoryIds;
    
    /**
     * 商圈ID列表
     */
    private List<Integer> circleIds;

    /**
     * 推荐顺序
     */
    private Integer recommendOrder;

    /**
     * 消费置顶 0否 1是
     */
    private Integer topConsumption;

    /**
     * 推荐置顶 0否 1是
     */
    private Integer topRecommend;
    
    /**
     * 商家管理人员列表
     */
    @NotEmpty(message = "商家管理人员不能为空")
    private List<ShopManager> managers;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 审核状态 1:通过；2:驳回
     */
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    /**
     * 启用状态
     */
    @NotNull(message = "启用状态不能为空")
    private Integer status=1;//默认是启用状态

    /**
     * 置顶开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime topStartTime;

    /**
     * 置顶结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime topEndTime;

    /**
     * 消费置顶开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime topConsumptionStartTime;

    /**
     * 消费置顶结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime topConsumptionEndTime;

}