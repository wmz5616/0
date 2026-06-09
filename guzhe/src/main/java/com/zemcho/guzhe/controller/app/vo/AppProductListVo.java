package com.zemcho.guzhe.controller.app.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @title: AppProductListVo
 * @Description:
 * @Date: 2026/5/7 11:40
 */
@Data
public class AppProductListVo {
    // 主键ID
    private Integer id;

    // 商家id
    private Integer shopId;

    // 商品编号
    private String productNo;

    // 商品封面图
    private String coverImage;

    // 商品轮播图（JSON格式）
    private String galleryImagesStr;

    // 商品轮播图
    private List<String> galleryImages;

    // 库存
    private Integer stock;

    // 商品名称
    private String name;

    // 规格
    private String specification;

    // 单位
    private String unit;

    // 原价，单位分
    private Integer price;

    // 是否开启折扣（0:否, 1:是）
    private Integer openDiscount;

    // 折扣
    private BigDecimal discountNum;

    // 是否开启折扣倒计时（0:否, 1:是）
    private Integer openDiscountTime;

    // 折扣倒计时
    private String discountTime;

    // 售价，单位分
    private Integer amount;

    // 排序
    private Integer sort;

    // 是否是虚拟商品（0:否, 1:是）
    private Integer isVirtual;

    // 有效期（天），虚拟商品才有
    private Integer timeLimit;

    // 销量
    private Integer saleNum;

    // 销量
    private String saleNumText;
}
