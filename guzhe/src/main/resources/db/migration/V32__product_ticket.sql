CREATE TABLE IF NOT EXISTS `product_ticket` (
    `id` int NOT NULL AUTO_INCREMENT,
    `product_id` int NOT NULL COMMENT '商品id',
    `ticket` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '券码',
    `sort` int NOT NULL COMMENT '序号',
    `status` int NOT NULL COMMENT '状态：1 未下发 2 已下发 3已核销',
    `order_id` int NOT NULL DEFAULT 0 COMMENT '订单id',
    `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    primary key (id),
    INDEX `idx_order_id`(`order_id`) USING BTREE,
    UNIQUE INDEX `idx_product_ticket`(`product_id`, `ticket`) USING BTREE
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT '商品-券码管理表' ROW_FORMAT = DYNAMIC;
