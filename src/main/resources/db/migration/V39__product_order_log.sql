CREATE TABLE IF NOT EXISTS `product_order_log` (
    `id` int NOT NULL AUTO_INCREMENT,
    `order_id` int NOT NULL DEFAULT 0 COMMENT '订单id',
    `order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单编号',
    `user_id` int NOT NULL DEFAULT 0 COMMENT '操作人id',
    `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '操作人名称',
    `handle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '操作',
    `details` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL DEFAULT '' COMMENT '详情',
    `create_time` datetime(0) NOT NULL COMMENT '创建时间',
    `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_order`(`order_id`) USING BTREE
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT '商品订单-操作明细表' ROW_FORMAT = DYNAMIC;
