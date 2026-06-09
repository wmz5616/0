
CREATE TABLE IF NOT EXISTS `shop` (
                        `id` int NOT NULL AUTO_INCREMENT,
                        `cover_image_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '店铺封面图URL',
                        `gallery_images` text COLLATE utf8mb4_general_ci COMMENT '店铺轮播图URL (JSON格式)',
                        `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '店铺名称',
                        `location` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '店铺经纬度',
                        `address` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '店铺详细地址',
                        `user_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '店铺联系人',
                        `phone` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '店铺联系电话',
                        `start_time` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '营业开始时间 , HH:mm:ss',
                        `end_time` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '营业结束时间 ，HH:mm:ss',
                        `top_recommend` int NOT NULL COMMENT '是否置顶推荐 0否 1是',
                        `top_start_time` datetime DEFAULT NULL COMMENT '置顶开始时间',
                        `top_end_time` datetime DEFAULT NULL COMMENT '置顶结束时间',
                        `recommend_order` int DEFAULT '0' COMMENT '推荐顺序',
                        `status` int NOT NULL COMMENT '店铺启用状态 0禁用 1启用',
                        `description` text COLLATE utf8mb4_general_ci NOT NULL COMMENT '店铺介绍（富文本内容）',
                        `remark` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                        `create_time` datetime NOT NULL COMMENT '创建时间',
                        `update_time` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商圈管理 - 店铺信息表';