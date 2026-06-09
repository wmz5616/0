package com.zemcho.guzhe.controller.product.param;

import lombok.Data;

import java.util.List;

/**
 * 批量更新商品库存和价格参数
 */
@Data
public class ProductStockUpdateParam {

    /**
     * 商品规格更新列表
     */
    private List<ProductSpecParam> specList;

    /**
     * 单个商品规格参数
     */
    @Data
    public static class ProductSpecParam {
        /**
         * 商品ID
         */
        private Integer productId;

        /**
         * 商品规格（只读，用于展示）
         */
        private String specification;

        /**
         * 原价（单位：分）
         */
        private Integer price;

        /**
         * 售价（单位：分）
         */
        private Integer amount;

        /**
         * 库存
         */
        private Integer stock;

        /**
         * 上架状态（1:上架, 2:下架）
         */
        private Integer status;
    }
}