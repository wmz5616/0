CREATE TABLE IF NOT EXISTS `screen_rental_order`
(
    `id`                   bigint       NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no`             varchar(32)  NOT NULL COMMENT '订单编号',
    `equipment_id`         bigint       NOT NULL COMMENT '设备ID',
    `shop_id`              bigint       NOT NULL COMMENT '商家ID',
    `shop_name`            varchar(100) NOT NULL COMMENT '商家名称',
    `business_circle_id`   bigint       NOT NULL COMMENT '商超ID',
    `business_circle_name` varchar(100) NOT NULL COMMENT '商超名称',
    `total_amount`         int          NOT NULL COMMENT '订单总金额（单位：分）',
    `order_time`           datetime     NOT NULL COMMENT '下单时间/支付时间',
    `user_id`              int          NOT NULL DEFAULT 0 COMMENT '下单用户id',
    `phone`                varchar(32)  NOT NULL DEFAULT '' COMMENT '下单手机号',
    `nick_name`            varchar(100) NOT NULL DEFAULT '' COMMENT '下单用户昵称',
    `status`               tinyint      NOT NULL DEFAULT 0 COMMENT '订单状态：0-待确认(未审核) 1-待生效(已审核未到租期) 2-生效中(已审核租期生效中) 3-已完成(租期全部结束) 4-已驳回(审核不通过) 5-已撤销(订单已撤销)',
    `remark`               varchar(255)          DEFAULT NULL COMMENT '备注/驳回原因',
    `create_time`          datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`          datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_equipment_id` (`equipment_id`),
    KEY `idx_shop_id` (`shop_id`),
    KEY `idx_business_circle_id` (`business_circle_id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='店位租用订单表';