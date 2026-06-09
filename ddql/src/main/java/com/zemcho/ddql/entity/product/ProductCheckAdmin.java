package com.zemcho.ddql.entity.product;

import lombok.Data;

@Data
public class ProductCheckAdmin {
    // 主键ID
    private Integer id;

    // 商品ID
    private Integer productId;

    // 管理员id
    private Integer adminId;
}
