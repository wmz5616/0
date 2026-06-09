CREATE TABLE IF NOT EXISTS `shop_manager` (
                                           `id` int NOT NULL AUTO_INCREMENT,
                                           `shop_id` int NOT NULL COMMENT '对应的商家',
                                           `sort` int NOT NULL COMMENT '排序',
                                           `name` varchar(20) NOT NULL COMMENT '姓名',
                                           `phone` varchar(20) NOT NULL COMMENT '手机号',
                                           `head_manager` tinyint NOT NULL COMMENT '是否是店长 1是 0否',
                                           primary key(id)
) COMMENT '商家-管理者表' ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
