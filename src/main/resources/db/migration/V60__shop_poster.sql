CREATE TABLE IF NOT EXISTS `shop_poster` (
    `id` int NOT NULL AUTO_INCREMENT,
    `shop_id` int NOT NULL DEFAULT 0 COMMENT '商家id',
    `url` varchar(255) NOT NULL DEFAULT '' COMMENT '海报图片url',
    `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    primary key(id),
    INDEX `idx_shop_id`(`shop_id`) USING BTREE
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT '商家-海报管理表' ROW_FORMAT = DYNAMIC;
