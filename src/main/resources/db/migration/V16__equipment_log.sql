CREATE TABLE IF NOT EXISTS `equipment_log`  (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `equipment_id` int UNSIGNED NOT NULL COMMENT '设备id',
    `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态: 1在线、2离线',
    `created_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `equipment_id`(`equipment_id`) USING BTREE
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '设备在线日志管理表' ROW_FORMAT = DYNAMIC;
