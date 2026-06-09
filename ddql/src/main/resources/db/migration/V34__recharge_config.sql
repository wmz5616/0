
CREATE TABLE IF NOT EXISTS `recharge_config` (
                `id` int NOT NULL AUTO_INCREMENT,
                `enable_recharge` int NOT NULL COMMENT '是否开启充值功能 0关闭 1开启',
                `enable_custom_amount` int NOT NULL COMMENT '是否开启自定义充值金额 0关闭 1开启',
                `min_amount` int DEFAULT NULL COMMENT '最低充值金额  单位为分',
                `critical_amount` int NOT NULL COMMENT '临界金额 单位为分',
                `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '充值说明',
                `create_time` datetime NOT NULL COMMENT '创建时间',
                `update_time` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='充值配置表';

-- ----------------------------
-- Records of recharge_config
-- ----------------------------
INSERT IGNORE INTO recharge_config VALUES (1, 1, 1, 100, 100, '', '2025-10-10 00:00:00', '2025-10-10 00:00:00');