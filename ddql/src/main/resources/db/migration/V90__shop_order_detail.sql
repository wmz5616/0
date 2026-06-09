CREATE TABLE IF NOT EXISTS `shop_order_detail`
(
    `id`            int          NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id`      int          NOT NULL COMMENT '订单ID',
    `order_no`      varchar(32)  NOT NULL COMMENT '订单编号',
    `item_name`     varchar(100) NOT NULL COMMENT '项目名称',
    `spec_name`     varchar(100) NOT NULL DEFAULT '' COMMENT '规格名称',
    `unit`          varchar(20)  NOT NULL DEFAULT '' COMMENT '单位',
    `unit_price`    int          NOT NULL DEFAULT 0 COMMENT '单价（单位：分）',
    `quantity`      int          NOT NULL DEFAULT 1 COMMENT '数量',
    `total_amount`  int          NOT NULL DEFAULT 0 COMMENT '行总金额（单位：分）',
    `deduct_coin`   int          NOT NULL DEFAULT 0 COMMENT '该行抵扣金币数',
    `deduct_amount` int          NOT NULL DEFAULT 0 COMMENT '该行抵扣金币金额（单位：分）',
    `pay_amount`    int          NOT NULL DEFAULT 0 COMMENT '该行实付金额（单位：分）',
    `sort`          int          NOT NULL DEFAULT 0 COMMENT '排序值',
    `remark`        varchar(255)          DEFAULT NULL COMMENT '备注',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_no` (`order_no`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='门店订单明细表';