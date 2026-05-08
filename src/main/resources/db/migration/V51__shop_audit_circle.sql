CREATE TABLE IF NOT EXISTS `shop_audit_circle` (
                                                   `id` int NOT NULL AUTO_INCREMENT,
                                                   `circle_id` int NOT NULL COMMENT '商圈ID',
                                                   `shop_audit_id` int NOT NULL COMMENT '店铺审核记录ID',
                                                   PRIMARY KEY (`id`) USING BTREE,
                                                   KEY `idx_shop_audit_id` (`shop_audit_id`),
                                                   KEY `idx_circle_id` (`circle_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='店铺审核-商圈关联表';
