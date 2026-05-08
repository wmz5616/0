-- ----------------------------
-- Table structure for daily_visit_trend
-- ----------------------------
CREATE TABLE IF NOT EXISTS `daily_visit_trend`  (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `date` date NOT NULL COMMENT '日期',
    `session_cnt` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '打开次数',
    `visit_pv` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '访问次数',
    `visit_uv` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '访问人数',
    `visit_uv_new` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '新用户数',
    `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
    `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `date`(`date`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户访问小程序数据日趋势管理表' ROW_FORMAT = Dynamic;