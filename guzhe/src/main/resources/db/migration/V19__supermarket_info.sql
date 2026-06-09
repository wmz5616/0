CREATE TABLE IF NOT EXISTS `supermarket_info` (
                                                  `id` int NOT NULL AUTO_INCREMENT,
                                                  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '封面图',
                                                  `logo_image` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商超轮播图',
                                                  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商超名称',
                                                  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '联系电话',
                                                  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '介绍信息',
                                                  `business_hours` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '营业时间',
                                                  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商超地址',
                                                  `longitude` varchar(50) DEFAULT NULL COMMENT '经度',
                                                  `latitude` varchar(50) DEFAULT NULL COMMENT '纬度',
                                                  `status` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：0禁用、1启用',
                                                  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
                                                  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                                  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
                                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商超信息管理表' ROW_FORMAT = Dynamic;
