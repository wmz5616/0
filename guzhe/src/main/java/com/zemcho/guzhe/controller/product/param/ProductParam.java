package com.zemcho.guzhe.controller.product.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.guzhe.entity.product.ProductSpec;
import com.zemcho.guzhe.entity.product.ProductSpecPrice;
import com.zemcho.guzhe.entity.product.ProductSpecValue;
import jakarta.validation.constraints.NotEmpty;
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
    @NotNull(message = "请上传商品封面图后再提交")
    private String coverImage;

    // 折扣
    private BigDecimal discountNum;

    // 商品轮播图（JSON格式）
    @NotNull(message = "请上传商品轮播图后再提交")
    private List<String> galleryImages;

    // 库存
    private Integer stock=0;

    // 商品名称
    @NotNull(message = "请填写商品名称后再提交")
    private String name;

    // 规格
    private String specification;

    // 单位
//    @NotNull(message = "请填写商品单位后再提交")
    private String unit="";

    // 原价，单位分
//    @NotNull(message = "请填写商品原价后再提交")
    private Integer price=0;

    // 是否开启折扣（0:否(默认), 1:是）
    private Integer openDiscount=0;

    // 是否开启折扣倒计时（0:否, 1:是）
    private Integer openDiscountTime=0;

    // 折扣倒计时
    private String discountTime;

    // 售价，单位分
    private Integer amount=0;

    // 商品分类id列表,逗号隔开
    @NotNull(message = "请选择商品分类后再提交")
    private String categoryIds;

    // 排序(默认为0，数值越大排在越前面)
    private Integer sort = 0;

    // 上架状态（1:上架, 2:下架，3:定时上架）
//    @NotNull(message = "请选择商品上架状态后再提交")
    private Integer status=0;

    // 定时上架时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledTime;

    // 是否是虚拟商品（0:否, 1:是）
    @NotNull(message = "请选择商品类型后再提交")
    private Integer isVirtual;

    // 核销人员id，虚拟商品才有
    private List<Integer> checkAdminIds;

    // 有效期（天），虚拟商品才有
    private Integer timeLimit = 0;

    // 商品详情（文件URL地址）
    @NotNull(message = "请填写商品详情后提交")
    private String detail;

    // 备注
    private String remark;

    //商品
    private List<ProductSpec> productSpec;

    private List<ProductSpecValue> productSpecValue;

    /**
     * 嵌套结构的规格数据（推荐使用）
     * 规格类型直接包含其下的规格值
     */
    private List<ProductSpecParam> specList;






}