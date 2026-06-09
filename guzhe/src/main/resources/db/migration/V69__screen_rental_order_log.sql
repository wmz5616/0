CREATE TABLE IF NOT EXISTS `screen_rental_order_log`
(
    `id`               bigint       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `order_id`         bigint       NOT NULL COMMENT '订单ID',
    `operation_type`   tinyint      NOT NULL COMMENT '操作类型：1-创建订单 2-编辑展示内容 3-后台审核 4-后台撤销',
    `operation_result` tinyint      NOT NULL DEFAULT 0 COMMENT '操作结果：0-无 1-确认 2-驳回',
    `operator_id`      int          NOT NULL DEFAULT 0 COMMENT '操作人ID',
    `operator_name`    varchar(100) NOT NULL DEFAULT '' COMMENT '操作人姓名',
    `operator_phone`   varchar(32)  NOT NULL DEFAULT '' COMMENT '操作人手机号',
    `display_type`     tinyint               DEFAULT NULL COMMENT '展示内容类型：1-商品 2-海报，仅编辑展示内容时有值',
    `operation_remark` varchar(255)          DEFAULT NULL COMMENT '操作说明：审核意见/驳回原因/撤销原因',
    `file_url`         varchar(255)          DEFAULT NULL COMMENT '上传文件URL',
    `operation_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_time` (`order_id`, `operation_time`),
    KEY `idx_operation_type` (`operation_type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='店位订单操作记录表';
