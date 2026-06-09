-- 商品规格价格库存表
CREATE TABLE `product_spec_price` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    `product_id` INT NOT NULL COMMENT '关联商品ID',
    `spec_value_ids` VARCHAR(200) NOT NULL COMMENT '规格值ID列表（逗号分隔）',
    `spec_combination` VARCHAR(200) NOT NULL COMMENT '规格组合（如：黄色,36码）',
    `price` INT DEFAULT 0 COMMENT '原价（分）',
    `amount` INT DEFAULT 0 COMMENT '售价（分）',
    `stock` INT DEFAULT 0 COMMENT '库存',
    `status` INT DEFAULT 2 COMMENT '上架状态（1:上架,2:下架）',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '删除时间',
    FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格价格库存表';