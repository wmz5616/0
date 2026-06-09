-- ----------------------------
-- Table structure for exchange_order_address
-- ----------------------------
CREATE TABLE IF NOT EXISTS `exchange_order_address` (
                         `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                         `order_id` bigint NOT NULL COMMENT '订单id',
                         `address_id` bigint NOT NULL COMMENT '用户收货地址记录id',
                         `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收货人名称',
                         `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '联系电话',
                         `region_id` int UNSIGNED NOT NULL COMMENT '所在地区id',
                         `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收货地址',
                         `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收货地址的经纬度',
                         `create_time` datetime NOT NULL COMMENT '创建时间',
                         `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                         PRIMARY KEY (`id`) USING BTREE,
                         INDEX `order_id`(`order_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='用户商品兑换订单-收货地址管理表';