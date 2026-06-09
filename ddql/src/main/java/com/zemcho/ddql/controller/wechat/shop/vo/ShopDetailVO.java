package com.zemcho.ddql.controller.wechat.shop.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 微信小程序商家详情VO
 */
@Data
public class ShopDetailVO {

    /**
     * 商家ID
     */
    private Integer id;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 店铺封面图URL
     */
    private String coverImageUrl;

    /**
     * 店铺轮播图URL列表
     */
    private List<String> galleryImages;

    /**
     * 店铺详细地址
     */
    private String address;

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
     * 店铺介绍（富文本内容）
     */
    private String description;

    /**
     * 行业类别名称
     */
    private String industryCategoryName;

    /**
     * 所属商圈名称
     */
    private String circleName;

    /**
     * 小程序点击次数
     */
    private Integer clickCount;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 用币规则信息
     */
    private CoinRuleVO coinRule;
}
