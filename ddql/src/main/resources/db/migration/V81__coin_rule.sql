CREATE TABLE IF NOT EXISTS `coin_rule` (
    `id` int NOT NULL AUTO_INCREMENT,
    `begin_amount` int NOT NULL COMMENT '起始金额 （元）',
    `threshold` int NOT NULL COMMENT '满减金额',
    `deduct` int NOT NULL COMMENT '扣减金币',
    `max_deduct` int NOT NULL COMMENT '最高抵扣金币',
    `shop_id` int NOT NULL COMMENT '对应的商家',
    `remark` text NULL COMMENT '抵扣规则说明',
    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    primary key(id)
) COMMENT '用币规则表'
