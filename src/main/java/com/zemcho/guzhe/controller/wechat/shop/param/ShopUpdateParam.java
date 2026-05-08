package com.zemcho.guzhe.controller.wechat.shop.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 编辑商家信息参数
 */
@Data
public class ShopUpdateParam {

    /**
     * 商家ID（必填）
     */
    @NotNull(message = "商家ID不能为空")
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
     * 店铺联系人（店长）
     */
    private String userName;

    /**
     * 店铺联系电话（店长电话）
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
     * 店铺详细地址
     */
    private String address;

    /**
     * 店铺经纬度
     */
    private String location;

    /**
     * 行业类别ID列表
     */
    private List<Integer> industryCategoryIds;

    /**
     * 所属商超ID列表
     */
    private List<Integer> circleIds;

    /**
     * 客服电话
     */
    private String customerPhone;

    /**
     * 客服微信二维码图片
     */
    private String customerCodeImg;

    /**
     * 商家介绍（富文本内容）
     */
    private String description;
}