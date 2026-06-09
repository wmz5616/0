SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `team_user`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `team_id` int UNSIGNED NOT NULL COMMENT '团队id',
  `user_id` int NOT NULL COMMENT '用户id',
  `type` int NOT NULL COMMENT '成员类型 0创建者 1管理员 2普通用户',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '成员在团队下的姓名',
  `user_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '成员在团队下的电话',
  `healthy_coin` int UNSIGNED NULL DEFAULT 0 COMMENT '用户在该团队下的健康币余额',
  `join_type` int NULL DEFAULT NULL COMMENT '加入的方式 0申请加入 1扫码加入',
  `status` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '启用状态 0启用 1禁用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '团队中的用户信息关联表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
