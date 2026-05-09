package com.zemcho.guzhe.controller.app.vo;

import com.zemcho.guzhe.entity.shop.ShopPoster;
import lombok.Data;

import java.util.List;

/**
 * @title: AppShopListVo
 * @Description:
 * @Date: 2026/5/7 10:43
 */
@Data
public class AppShopListVo {
    // 主键ID
    private Integer id;

    // 商家封面图URL
    private String coverImageUrl;

    // 商家轮播图URL (JSON格式)
    private String galleryImagesStr;

    // 商家轮播图URL
    private List<String> galleryImages;

    // 商家名称
    private String name;

    // 状态：0禁用 1启用
    private Integer status;

    // 客服电话
    private String customerPhone;

    // 客服微信二维码图片
    private String customerCodeImg;

    // 商家海报信息
    private List<ShopPoster> posterList;
}
