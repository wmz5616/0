CREATE TABLE IF NOT EXISTS `app_version_push_log` (
                                        `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                        `serial_number` VARCHAR(50) NOT NULL COMMENT '设备编号',
                                        `device_id` INT DEFAULT NULL COMMENT '设备ID',
                                        `sup_id` INT DEFAULT NULL COMMENT '商超ID',
                                        `status` TINYINT NOT NULL COMMENT '下发状态：1-成功，2-失败',
                                        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='APP版本下发日志表';
