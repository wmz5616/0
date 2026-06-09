CREATE TABLE IF NOT EXISTS `cas_user_sport_record`  (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL DEFAULT 0 COMMENT '用户id',
    `date` date NULL DEFAULT NULL COMMENT '日期',
    `step_num` int NOT NULL DEFAULT 0 COMMENT '当天总步数',
    `check_in_time` int NOT NULL DEFAULT 0 COMMENT '当天打卡总时长（秒）',
    `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
    `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `user_date`(`user_id`,`date`) USING BTREE
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户每天运动记录表' ROW_FORMAT = DYNAMIC;
