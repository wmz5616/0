CREATE TABLE IF NOT EXISTS `product_category_relation` (
   `id` int NOT NULL AUTO_INCREMENT,
   `product_id` int NOT NULL COMMENT '商品id',
   `category_id` int NOT NULL COMMENT '分类id',
    primary key (id)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT '商品-分类-关联表' ROW_FORMAT = DYNAMIC;
