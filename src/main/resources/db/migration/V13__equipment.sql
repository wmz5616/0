-- 创建新的设备表
CREATE TABLE `equipment` (
                             `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                             `serial_number` VARCHAR(50) NOT NULL COMMENT '设备编号（唯一）',
                             `supermarket_id` INT UNSIGNED NOT NULL COMMENT '所属商超ID',
                             `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '客服电话',
                             `online_status` INT DEFAULT NULL COMMENT '设备状态：0-在线，1-离线',
                             `status` INT DEFAULT NULL COMMENT '启用状态：0-禁用，1-启用',
                             `money` INT DEFAULT NULL COMMENT '店位每月租金（分）',
                             `sort` INT DEFAULT NULL COMMENT '排序（数值大的排前面）',
                             `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                             `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             `delete_time` DATETIME DEFAULT NULL COMMENT '删除时间（软删除）',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE KEY `uk_serial_number` (`serial_number`),
                             KEY `idx_supermarket_id` (`supermarket_id`),
                             KEY `idx_status` (`status`),
                             KEY `idx_delete_time` (`delete_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='设备表';
