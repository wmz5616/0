-- ----------------------------
-- Table structure for exchange_order_log
-- ----------------------------
CREATE TABLE IF NOT EXISTS `exchange_order_log` (
                             `id` int NOT NULL AUTO_INCREMENT,
                             `order_id` bigint NOT NULL COMMENT '订单id',
                             `order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单编号',
                             `user_id` int NOT NULL COMMENT '操作人id',
                             `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作人名称',
                             `handle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作',
                             `details` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '详情',
                             `create_time` datetime NOT NULL COMMENT '创建时间',
                             `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `order_id`(`order_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='用户商品兑换订单操作明细表';