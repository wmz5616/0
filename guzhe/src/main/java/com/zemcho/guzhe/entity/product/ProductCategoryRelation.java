package com.zemcho.guzhe.entity.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品分类关联实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryRelation {
    // 主键ID
    private Integer id;

    // 商品id
    private Integer productId;

    // 分类id
    private Integer categoryId;
}
