
CREATE TABLE IF NOT EXISTS `business_circle` (
            `id` int NOT NULL AUTO_INCREMENT,
            `cover_image_url` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '商圈封面图URL',
            `logo_image_url` text COLLATE utf8mb4_general_ci COMMENT '商圈轮播图URL',
            `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '商圈名称',
            `location` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '商圈经纬度 经度 , 维度',
            `location_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商圈详细地址名称',
            `sort_order` int DEFAULT NULL COMMENT '排序值，默认为0，数值越大排在前面',
            `status` int NOT NULL DEFAULT '1' COMMENT '启用状态 0禁用 1启用',
            `description` text COLLATE utf8mb4_general_ci NOT NULL COMMENT '商圈介绍',
            `remark` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
            `create_time` datetime NOT NULL COMMENT '创建时间',
            `update_time` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商圈信息表';