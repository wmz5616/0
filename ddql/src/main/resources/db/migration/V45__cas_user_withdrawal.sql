CREATE TABLE IF NOT EXISTS `cas_user_withdrawal`  (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL DEFAULT 0 COMMENT '用户id',
    `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
    `phone` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户手机号',
    `team_id` int NOT NULL DEFAULT 0 COMMENT '团队id',
    `team_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '团队名字',
    `team_type` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '团队类型 0企事单位 1政府部分 2家庭 3朋友',
    `amount` int NOT NULL DEFAULT 0 COMMENT '提现金额（元），只能为整数，一元对应一个健康币',
    `out_bill_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '系统生成的订单号',
    `wx_transaction_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '微信转账单号',
    `state` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '状态',
    `wx_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '微信响应结果信息（json格式）',
    `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
    `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `user_id`(`user_id`) USING BTREE
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户提现记录表' ROW_FORMAT = DYNAMIC;
