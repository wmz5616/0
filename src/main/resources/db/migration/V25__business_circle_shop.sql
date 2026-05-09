
CREATE TABLE IF NOT EXISTS `business_circle_shop` (
                    `id` int NOT NULL AUTO_INCREMENT,
                    `circle_id` int NOT NULL COMMENT '商超id',
                    `shop_id` int NOT NULL COMMENT '店铺id',
                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商家-商超关联表';