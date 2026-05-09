CREATE TABLE IF NOT EXISTS `screen_rental_detail`
(
    `id`           bigint   NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id`     bigint   NOT NULL COMMENT '订单ID',
    `equipment_id`    bigint   NOT NULL COMMENT '设备ID',
    `shop_id`   bigint         NOT NULL COMMENT '商家ID',
    `business_circle_id` bigint NOT NULL COMMENT '商超ID',
    `rental_year`  smallint NOT NULL COMMENT '租用年份，如2026',
    `rental_month` tinyint  NOT NULL COMMENT '租用月份，如4（代表4月）',
    `slot_count`   tinyint  NOT NULL DEFAULT 1 COMMENT '占用店位数，固定为1',
    `status`               tinyint      NOT NULL DEFAULT 0 COMMENT '订单状态：0-待确认(未审核) 1-待生效(已审核未到租期) 2-生效中(已审核租期生效中) 3-已完成(租期全部结束) 4-已驳回(审核不通过) 5-已撤销(订单已撤销)',
    `create_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_device_year_month` (`equipment_id`, `rental_year`, `rental_month`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='店位租用明细表';