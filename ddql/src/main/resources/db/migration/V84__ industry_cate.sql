CREATE TABLE IF NOT EXISTS `industry_category` (
                                           `id` int NOT NULL AUTO_INCREMENT,
                                           `name` varchar(50) NOT NULL COMMENT '行业类别名称',
                                           `sort` int NOT NULL COMMENT '排序',
                                           primary key(id)
)COMMENT '店铺经营行业类别表'
