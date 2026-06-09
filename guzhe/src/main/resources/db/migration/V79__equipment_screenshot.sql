CREATE TABLE IF NOT EXISTS `equipment_screenshot`
(
    `id`                bigint       NOT NULL AUTO_INCREMENT COMMENT 'id',
    `equipment_id`      bigint       NOT NULL DEFAULT 0 COMMENT '设备id',
    `serial_number`     varchar(64)  NOT NULL DEFAULT '' COMMENT '设备编号',
    `screenshot_url`    varchar(255) NOT NULL DEFAULT '' COMMENT '截图路径url',
    `screenshot_status` tinyint      NOT NULL DEFAULT 0 COMMENT '截图状态：0-待截图 1-成功 2-失败',
    `fail_reason`       varchar(255) NOT NULL DEFAULT '' COMMENT '截图失败原因',
    `create_time`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_equipment_id` (`equipment_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='设备截图表';
