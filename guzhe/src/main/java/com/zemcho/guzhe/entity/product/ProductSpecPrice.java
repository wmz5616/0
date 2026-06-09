package com.zemcho.guzhe.entity.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品规格价格库存实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSpecPrice {
    // 主键ID
    private Integer id;

    // 关联商品ID
    private Integer productId;

    // 规格值ID列表（逗号分隔）
    private String specValueIds;

    // 规格组合（如：黄色,36码）
    private String specCombination;

    // 原价，单位分
    private Integer price;

    // 售价，单位分
    private Integer amount;

    // 库存
    private Integer stock;

    // 上架状态（1:上架, 2:下架）
    private Integer status;

    // 排序
    private Integer sort;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 删除时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteTime;
}