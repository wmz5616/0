package com.zemcho.guzhe.entity.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品规格值实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSpecValue {
    // 主键ID
    private Integer id;

    // 关联规格类型ID
    private Integer typeId;

    // 规格值（如：黄色、36码）
    private String valueName;

    // 排序
    private Integer sort;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}