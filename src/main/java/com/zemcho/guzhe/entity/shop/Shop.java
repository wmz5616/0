package com.zemcho.guzhe.entity.shop;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Shop {
    // 主键ID
    private Integer id;

    // 店铺封面图URL
    private String coverImageUrl;

    // 店铺轮播图URL (JSON格式)
    private String galleryImages;

    // 店铺名称
    private String name;

    // 店铺经纬度
    private String location;

    // 店铺详细地址
    private String address;

    // 店铺联系人
    private String userName;

    // 店铺联系电话
    private String phone;

    // 营业开始时间 , HH:mm:ss
    private String startTime;

    // 营业结束时间 ，HH:mm:ss
    private String endTime;

    // 是否置顶推荐 0否 1是
    private Integer topRecommend;

    // 置顶开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime topStartTime;

    // 置顶结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime topEndTime;

    // 推荐顺序
    private Integer recommendOrder;

    // 是否消费置顶 0否 1是
    private Integer topConsumption;

    // 置顶开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime topConsumptionStartTime;

    // 置顶结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime topConsumptionEndTime;

    // 店铺启用状态 0禁用 1启用
    private Integer status;

    // 资质认证 0 未认证 1 待审核 2 已通过 3 已驳回
    private Integer qualificationCert;

    // 店铺状态 0 正常 1禁用 2 已注销
    private Integer shopStatus;

    // 是否开启收款
    private Integer receiptStatus;

    // 店铺介绍（富文本内容）
    private String description;

    // 备注
    private String remark;

    // 绑定的商户id
    private Integer merchantId;

    // 抽成比例 单位百分之1
    private Integer rate;

    // 合同照片
    private String contract;

    // 客服电话
    private String customerPhone;

    // 客服微信二维码图片
    private String customerCodeImg;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    // 修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
