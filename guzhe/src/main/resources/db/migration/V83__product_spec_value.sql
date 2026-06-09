-- 商品规格值表
CREATE TABLE `product_spec_value` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    `type_id` INT NOT NULL COMMENT '关联规格类型ID',
    `value_name` VARCHAR(50) NOT NULL COMMENT '规格值（如：黄色、36码）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `delete_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '删除时间',
    FOREIGN KEY (`type_id`) REFERENCES `product_spec_type`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格值表';