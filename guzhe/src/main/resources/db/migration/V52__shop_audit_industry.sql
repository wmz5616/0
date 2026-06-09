CREATE TABLE IF NOT EXISTS `shop_audit_industry` (
                                                     `id` int NOT NULL AUTO_INCREMENT,
                                                     `industry_category_id` int NOT NULL COMMENT '行业类别ID',
                                                     `shop_audit_id` int NOT NULL COMMENT '店铺审核记录ID',
                                                     PRIMARY KEY (`id`) USING BTREE,
                                                     KEY `idx_shop_audit_id` (`shop_audit_id`),
                                                     KEY `idx_industry_category_id` (`industry_category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='店铺审核-行业类别关联表';
