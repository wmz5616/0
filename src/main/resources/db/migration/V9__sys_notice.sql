-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_notice`  (
                            `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                            `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文章标题',
                            `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '正文内容',
                            `status` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：0不发布，1未发布，2已发布',
                            `is_publish` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否发布 0否 1是',
                            `publish_time` datetime NULL COMMENT '发布时间',
                            `create_time` datetime NOT NULL COMMENT '创建时间',
                            `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知公告管理表' ROW_FORMAT = DYNAMIC;
