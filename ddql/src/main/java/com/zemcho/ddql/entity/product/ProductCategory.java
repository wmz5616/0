package com.zemcho.ddql.entity.product;

import lombok.Data;

@Data
public class ProductCategory {
    // 主键ID
    private Integer id;

    // 商品ID
    private Integer productId;

    // 分类ID
    private Integer categoryId;

}
