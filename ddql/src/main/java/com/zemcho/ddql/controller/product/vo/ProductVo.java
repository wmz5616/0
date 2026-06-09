package com.zemcho.ddql.controller.product.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.ddql.util.excel.converter.common.YesOrNoConverter;
import com.zemcho.ddql.util.excel.converter.product.PayWayConverter;
import com.zemcho.ddql.util.excel.converter.product.ProductCategoryConverter;
import com.zemcho.ddql.util.excel.converter.product.ProductStatusConverter;
import lombok.Data;

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

    // 单位
    @ExcelProperty(value = "单位")
    @ColumnWidth(15)
    private String unit;

    // 库存
    @ExcelProperty(value = "商品库存")
    @ColumnWidth(20)
    private Integer stock;

    // 兑换币额
    @ExcelProperty(value = "兑换币额")
    @ColumnWidth(20)
    private Integer exchangeAmount;

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

    // 支付方式
    @ExcelProperty(value = "支付方式", converter = PayWayConverter.class)
    @ColumnWidth(20)
    private Integer payWay;

    // 支付金额 单位分
    @ExcelProperty(value = "支付金额")
    @ColumnWidth(20)
    private Long payAmount;

    // 备注
    @ExcelProperty(value = "备注")
    @ColumnWidth(20)
    private String remark;

    // 兑换数量
    @ExcelIgnore
    private Integer exchangeNum;

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
