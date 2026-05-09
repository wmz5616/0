SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE `sys_article` (
                               `id` int unsigned NOT NULL AUTO_INCREMENT,
                               `status` int unsigned DEFAULT NULL COMMENT '是否启用 0启用 1关闭',
                               `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '标题',
                               `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '文本内容',
                               `create_time` datetime NOT NULL COMMENT '创建时间',
                               `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='系统公告表';
SET FOREIGN_KEY_CHECKS = 1;