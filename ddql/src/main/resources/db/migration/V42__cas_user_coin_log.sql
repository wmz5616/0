CREATE TABLE IF NOT EXISTS `cas_user_coin_log`  (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `txn_type` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '类型：1打卡、2充值、3兑换、4提现',
    `txn_id` int NOT NULL DEFAULT 0 COMMENT '对应类型的记录id',
    `coin_type` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '币类型：1健康币、2金币',
    `num_type` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '变更类型：1增加、2减少',
    `coin_num` int NOT NULL DEFAULT 0 COMMENT '币数量',
    `user_id` int NOT NULL DEFAULT 0 COMMENT '用户id',
    `team_id` int NOT NULL DEFAULT 0 COMMENT '团队id',
    `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注说明',
    `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
    `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `team_id`(`team_id`) USING BTREE,
    INDEX `user_txn_type`(`user_id`, `txn_type`) USING BTREE
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户币变更记录表' ROW_FORMAT = DYNAMIC;
