package com.zemcho.guzhe.controller.product.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.guzhe.util.excel.converter.common.YesOrNoConverter;
import com.zemcho.guzhe.util.excel.converter.product.ProductCategoryConverter;
import com.zemcho.guzhe.util.excel.converter.product.ProductStatusConverter;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductVo {
    // 主键ID
    @ExcelProperty(value = "商品id")
    @ColumnWidth(15)
    private Integer id;

    // 商品编号
    @ExcelProperty(value = "商品编号")
    @ColumnWidth(25)
    private String productNo;

    // 商品封面图
    @ExcelIgnore
    private String coverImage;

    // 商品轮播图（JSON格式）
    @ExcelIgnore
    private List<String> galleryImages;

    // 商品名称
    @ExcelProperty(value = "商品名称")
    @ColumnWidth(25)
    private String name;

    // 规格
    @ExcelProperty(value = "规格")
    @ColumnWidth(15)
    private String specification;

    // 是否开启折扣（0:否, 1:是）
    private Integer openDiscount;

    // 折扣
    private BigDecimal discountNum;

    // 是否开启折扣倒计时（0:否, 1:是）
    private Integer openDiscountTime;

    // 折扣倒计时
    private String discountTime;

    // 原价，单位分
    private Integer price;

    // 单位
    @ExcelProperty(value = "单位")
    @ColumnWidth(15)
    private String unit;

    // 库存
    @ExcelProperty(value = "商品库存")
    @ColumnWidth(20)
    private Integer stock;
    //售价
    private Integer admount; // 数据库存的“分”

    // 销量
    private Integer saleNum;

    // 关联分类集合
    @ExcelProperty(value = "商品分类", converter = ProductCategoryConverter.class)
    @ColumnWidth(25)
    private List<CategoryVo> categoryList;

    // 排序
    @ExcelIgnore
    private Integer sort;

    // 上架状态（1:上架, 2:下架，3:定时上架）
    @ExcelProperty(value = "上架状态", converter = ProductStatusConverter.class)
    @ColumnWidth(20)
    private Integer status;

    // 定时上架时间
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledTime;

    // 是否是虚拟商品（0:否, 1:是）
    @ExcelProperty(value = "上架状态", converter = YesOrNoConverter.class)
    @ColumnWidth(20)
    private Integer isVirtual;

    // 有效期（天），虚拟商品才有
    @ExcelIgnore
    private Integer timeLimit;

    // 核销人员id，虚拟商品才有
    @ExcelIgnore
    private List<Integer> checkAdminIds;

    // 商品详情（富文本）
    @ExcelIgnore
    private String detail;

    // 备注
    @ExcelProperty(value = "备注")
    @ColumnWidth(20)
    private String remark;

    // 创建时间
    @ExcelProperty(value = "创建时间")
    @ColumnWidth(20)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
