package com.zemcho.ddql.controller.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShopVO {
    // 主键ID
    private Integer id;

    // 店铺封面图URL
    private String coverImageUrl;

    // 店铺轮播图URL (JSON格式)
    private String galleryImagesStr;

    // 店铺轮播图URL (JSON格式)
    private List<String> galleryImages;

    // 店铺名称
    private String name;

    // 所属商圈名称列表
    private List<String> circleNameList;

    private List<ShopCircleListVO> circleList;

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

    // 营业时间
    private String businessTime;

    // 是否置顶推荐 0否 1是
    private Integer topRecommend;

    // 置顶开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime topStartTime;

    // 置顶结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime topEndTime;

    // 是否消费置顶 0否 1是
    private Integer topConsumption;

    // 置顶开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime topConsumptionStartTime;

    // 置顶结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime topConsumptionEndTime;

    private Integer shopStatus;

    private Integer rate;

    private Integer qualificationCert;

    // 推荐顺序
    private Integer recommendOrder;

    // 小程序点击次数
    private Integer clickCount;

    // 店铺距离 km为单位
    private Double distance;

    // 店铺启用状态 0禁用 1启用
    private Integer status;

    // 店铺介绍（富文本内容）
    private String description;

    // 备注
    private String remark;

    //合同照片
    private String contract;

    //商家收款配置
    private Integer receiptStatus;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    // 修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
