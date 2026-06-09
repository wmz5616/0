CREATE TABLE IF NOT EXISTS `shop_industry_category` (
                                                   `id` int NOT NULL AUTO_INCREMENT,
                                                   `industry_category_id` INT NOT NULL COMMENT '行业类别ID',
                                                   `shop_id` int NOT NULL COMMENT '商家',
                                                   primary key(id)
)COMMENT '商家-经营行业类别关联表' ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
