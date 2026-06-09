package com.zemcho.ddql.controller.wechat.shop.param;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AuditShopParam {

    // 主键ID
    private Integer id;

    // 店铺封面图URL
    @NotBlank(message = "封面图URL不能为空")
    private String coverImageUrl;

    // 店铺轮播图URL (JSON格式)
    @NotBlank(message = "轮播图URL不能为空")
    private String galleryImages;

    // 店铺名称
    @NotBlank(message = "名称不能为空")
    private String name;

    // 店铺经纬度
    @NotBlank(message = "经纬度不能为空")
    private String location;

    // 店铺详细地址
    @NotBlank(message = "地址不能为空")
    private String address;

    // 商家联系人 （店长）
    @NotBlank(message = "联系人不能为空")
    private String userName;

    // 店铺联系电话（店长电话）
    @NotBlank(message = "电话不能为空")
    private String phone;

    // 营业开始时间 , HH:mm:ss
    private String startTime;

    // 营业结束时间 ，HH:mm:ss
    private String endTime;

    // 营业时间
    private String businessTime;

    // 商圈Ids
    private List<Integer> circleIds;

    // 行业类别
    @NotEmpty(message = "行业类别不能为空")
    private List<Integer> industryCategoryIds;

    // 商家介绍 （富文本内容）
    @NotBlank(message = "介绍不能为空")
    private String description;

}
