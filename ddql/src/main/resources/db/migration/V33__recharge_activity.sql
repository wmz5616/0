
CREATE TABLE IF NOT EXISTS `recharge_activity` (
                                     `id` int NOT NULL AUTO_INCREMENT,
                                     `recharge_amount` int NOT NULL COMMENT '充值金额 单位为分',
                                     `enable_gift` int NOT NULL COMMENT '是否开启赠送金额 0关闭 1开启',
                                     `gift_amount` int DEFAULT NULL COMMENT '赠送金额  单位为分',
                                     `recharge_count` int NOT NULL COMMENT '活动充值次数限制 ，0为不限制',
                                     `sort` int NOT NULL COMMENT '展示顺序',
                                     `create_time` datetime NOT NULL COMMENT '创建时间',
                                     `update_time` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='充值活动表';