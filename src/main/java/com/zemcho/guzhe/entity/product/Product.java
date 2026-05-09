package com.zemcho.guzhe.entity.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    // 主键ID
    private Integer id;

    // 商家id
    private Integer shopId;

    // 商品编号
    private String productNo;

    // 商品封面图
    private String coverImage;

    // 商品轮播图（JSON格式）
    private String galleryImages;

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

    // 上架状态（1:上架, 2:下架，3:定时上架）
    private Integer status;

    // 定时上架时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledTime;

    // 是否是虚拟商品（0:否, 1:是）
    private Integer isVirtual;

    // 有效期（天），虚拟商品才有
    private Integer timeLimit;

    // 商品详情（富文本）
    private String detail;

    // 备注
    private String remark;

    // 销量
    private Integer saleNum;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
