CREATE TABLE IF NOT EXISTS `product_check_admin` (
                                                     `id` int NOT NULL AUTO_INCREMENT,
                                                     `product_id` int NOT NULL DEFAULT 0 COMMENT '商品id',
                                                     `admin_id` int NOT NULL DEFAULT 0 COMMENT '管理员id',
                                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='商品-核销人员关联表';