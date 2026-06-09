CREATE TABLE IF NOT EXISTS `delivery_address` (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '关联的用户id',
    `is_default` int NOT NULL DEFAULT 0 COMMENT '是否是默认 0否 1是',
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货人名称',
    `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '联系电话',
    `region_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '所在地区id',
    `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货地址',
    `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货地址的经纬度',
    `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    primary key (id),
    INDEX `idx_user_default`(`user_id`, `is_default`) USING BTREE
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT '用户-收货地址管理表' ROW_FORMAT = DYNAMIC;
