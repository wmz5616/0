CREATE TABLE IF NOT EXISTS `cas_user_check_in_team_record`  (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `record_id` int NOT NULL DEFAULT 0 COMMENT '打卡记录id',
    `team_id` int NOT NULL DEFAULT 0 COMMENT '团队id',
    `team_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '团队名字',
    `team_type` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '团队类型 0企事单位 1政府部分 2家庭 3朋友',
    `user_id` int NOT NULL DEFAULT 0 COMMENT '用户id',
    `place_id` int NOT NULL DEFAULT 0 COMMENT '场地id',
    `check_in_method` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '打卡方式 0扫码打卡 1距离打卡',
    `date` date NULL DEFAULT NULL COMMENT '打卡日期',
    `obtain_type` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '获取类型：1时长、2步数、3时长+步数',
    `health_coin` int NOT NULL DEFAULT 0 COMMENT '本次打卡获得的团队健康币数量',
    `rank` int NOT NULL DEFAULT 0 COMMENT '排名',
    `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
    `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `record_id`(`record_id`) USING BTREE
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户打卡-团队记录表' ROW_FORMAT = DYNAMIC;
