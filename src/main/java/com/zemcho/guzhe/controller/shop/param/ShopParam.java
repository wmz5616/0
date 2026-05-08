package com.zemcho.guzhe.controller.shop.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShopParam {
    // 主键ID
    private Integer id = 0;

    // 店铺封面图URL
    private String coverImageUrl;

    // 店铺轮播图URL (JSON格式)
    private List<String> galleryImages;

    // 店铺名称 店铺名称唯一
    @NotNull(message = "名称不能为空")
    private String name;

    // 店铺经纬度
    @NotNull(message = "经纬度不能为空")
    private String location;

    // 店铺详细地址
    private String address;

    // 店铺联系人
    private String userName;

    // 店铺联系电话
    private String phone = "";

    // 营业开始时间 , HH:mm:ss
    private String startTime;

    // 营业结束时间 ，HH:mm:ss
    private String endTime;

    // 商圈id列表
//    @NotNull(message = "商圈id列表不能为空")
    private List<Integer> circleIds;

    // 行业类别分类id
    @NotNull(message = "行业类别不能为空")
    private List<Integer> industryCategoryIds;

    // 是否置顶推荐 0否 1是
    private Integer topRecommend;

    // 置顶开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime topStartTime;

    // 置顶结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime topEndTime;

    // 是否消费置顶 0否 1是
    @NotNull(message = "消费置顶不能为空")
    private Integer topConsumption;

    // 消费置顶开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime topConsumptionStartTime;

    // 消费置顶结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime topConsumptionEndTime;

    // 推荐顺序
    private Integer recommendOrder;

    // 店铺启用状态 0禁用 1启用
    @NotNull(message = "启用状态不能为空")
    private Integer status;

    // 店铺介绍（富文本内容）
    @NotNull(message = "介绍不能为空")
    private String description;

    // 备注
    private String remark;

    // 客服电话
    private String customerPhone;

    // 客服微信二维码图片
    private String customerCodeImg;
}
