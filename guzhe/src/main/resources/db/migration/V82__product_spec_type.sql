-- 商品规格类型表
CREATE TABLE `product_spec_type` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    `product_id` INT NOT NULL COMMENT '关联商品ID',
    `type_name` VARCHAR(50) NOT NULL COMMENT '规格类型名称',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `delete_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '删除时间',
    FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格类型表';