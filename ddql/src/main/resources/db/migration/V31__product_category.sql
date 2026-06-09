
CREATE TABLE IF NOT EXISTS `product_category` (
                    `id` int NOT NULL AUTO_INCREMENT,
                    `product_id` int NOT NULL COMMENT '商品id',
                    `category_id` int NOT NULL COMMENT '分类id',
                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='商品管理 - 商品分类关联表';