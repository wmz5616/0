CREATE TABLE IF NOT EXISTS `certification_log`  (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `card_num` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '身份证号码',
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '姓名',
    `birth_date` date NULL DEFAULT NULL COMMENT '生日',
    `area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '籍贯',
    `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '实名认证详情json格式',
    `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `card_num`(`card_num`) USING BTREE
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '实名认证记录表' ROW_FORMAT = DYNAMIC;
