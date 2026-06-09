package com.zemcho.ddql.controller.product.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductParam {

    // 主键ID
    private Integer id = 0;

    // 商品编号
    private String productNo;

    // 商品封面图
    private String coverImage;

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

    // 兑换币额
    private Integer exchangeAmount;

    // 商品分类id列表,逗号隔开
    @NotNull(message = "参数{categoryList}为空")
    private String categoryIds;

    // 排序
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

    // 支付方式 0 金币 1组合 2 现金
    private Integer payWay;

    // 支付金额 单位分
    private Integer payAmount;

    // 备注
    private String remark;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
