package com.zemcho.guzhe.controller.product.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductParam {

    // 主键ID
    private Integer id = 0;

    // 商家id
    private Integer shopId;

    // 商品编号
    private String productNo;

    // 商品封面图
    private String coverImage;

    // 折扣
    private BigDecimal discountNum;

    // 商品轮播图（JSON格式）
    private List<String> galleryImages;

    // 库存
    private Integer stock;

    // 商品名称
    @NotNull(message = "参数{name}为空")
    private String name;

    // 规格
    private String specification;

    // 单位
    @NotNull(message = "参数{unit}为空")
    private String unit;

    // 原价，单位分
    @NotNull(message = "参数{price}为空")
    private Integer price;

    // 是否开启折扣（0:否(默认), 1:是）
    private Integer openDiscount=0;

    // 是否开启折扣倒计时（0:否, 1:是）
    private Integer openDiscountTime=0;

    // 折扣倒计时
    private String discountTime;

    // 售价，单位分
    private Integer amount;

    // 商品分类id列表,逗号隔开
    @NotNull(message = "参数{categoryList}为空")
    private String categoryIds;

    // 排序(默认为0，数值越大排在越前面)
    private Integer sort = 0;

    // 上架状态（1:上架, 2:下架，3:定时上架）
    @NotNull(message = "参数{status}为空")
    private Integer status;

    // 定时上架时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledTime;

    // 是否是虚拟商品（0:否, 1:是）
    @NotNull(message = "参数{isVirtual}为空")
    private Integer isVirtual;

    // 核销人员id，虚拟商品才有
    private List<Integer> checkAdminIds;

    // 有效期（天），虚拟商品才有
    private Integer timeLimit = 0;

    // 商品详情（富文本）
    @NotNull(message = "参数{detail}为空")
    private String detail;

    // 备注
    private String remark;


}