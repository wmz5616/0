
CREATE TABLE IF NOT EXISTS `product` (
            `id` int NOT NULL AUTO_INCREMENT,
            `product_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品编号',
            `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品封面图',
            `gallery_images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '商品轮播图（JSON格式）',
            `stock` int NOT NULL COMMENT '库存',
            `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
            `specification` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '规格',
            `unit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '单位',
            `exchange_amount` int NOT NULL COMMENT '兑换币额',
            `sort` int DEFAULT '0' COMMENT '排序',
            `status` tinyint NOT NULL DEFAULT '2' COMMENT '上架状态（1:上架, 2:下架，3:定时上架）',
            `scheduled_time` datetime DEFAULT NULL COMMENT '定时上架时间',
            `is_virtual` tinyint NOT NULL DEFAULT '0' COMMENT '是否是虚拟商品（0:否, 1:是）',
            `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品详情（富文本）',
            `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
            `create_time` datetime NOT NULL COMMENT '创建时间',
            `update_time` datetime NOT NULL COMMENT '更新时间',
            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品表';

