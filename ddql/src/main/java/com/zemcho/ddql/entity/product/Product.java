package com.zemcho.ddql.entity.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Product {
    // 主键ID
    private Integer id;

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

    // 兑换币额
    private Integer exchangeAmount;

    // 排序
    private Integer sort;

    // 上架状态（1:上架, 2:下架，3:定时上架）
    private Integer status;

    // 定时上架时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime scheduledTime;

    // 是否是虚拟商品（0:否, 1:是）
    private Integer isVirtual;

    // 有效期（天），虚拟商品才有
    private Integer timeLimit;

    // 商品详情（富文本）
    private String detail;

    // 备注
    private String remark;

    // 兑换数量
    private Integer exchangeNum;

    // 支付方式
    private Integer PayWay;

    // 支付金额 单位分
    private Long payAmount;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
