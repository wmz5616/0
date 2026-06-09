
CREATE TABLE IF NOT EXISTS `product_ticket` (
                    `id` int NOT NULL AUTO_INCREMENT,
                    `product_id` int NOT NULL COMMENT '商品id',
                    `ticket` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '券码',
                    `sort` int NOT NULL COMMENT '序号',
                    `status` int NOT NULL COMMENT '状态：1 未下发 2 已下发 3已核销',
                    `order_id` int NOT NULL DEFAULT 0 COMMENT '订单id',
                    `create_time` datetime NOT NULL COMMENT '创建时间',
                    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                    PRIMARY KEY (`id`) USING BTREE,
                    INDEX `order_id`(`order_id`) USING BTREE,
                    INDEX `product_id`(`product_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='虚拟商品 - 券码表';