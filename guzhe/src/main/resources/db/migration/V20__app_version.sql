CREATE TABLE IF NOT EXISTS `app_version` (
                               `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                               `serial_number` VARCHAR(50) NOT NULL COMMENT '版本编号',
                               `file_url` VARCHAR(500) NOT NULL COMMENT '文件URL',
                               `release` INT DEFAULT 0 COMMENT '排序字段',
                               `is_publish` TINYINT DEFAULT 1 COMMENT '是否发布：0-发布，1-不发布',
                               `name` VARCHAR(100) DEFAULT NULL COMMENT '名称',
                               `remark` TEXT COMMENT '备注',
                               `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `delete_time` DATETIME DEFAULT NULL COMMENT '删除时间（软删除）',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='APP版本管理表';
