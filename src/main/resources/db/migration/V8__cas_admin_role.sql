-- ----------------------------
-- Table structure for cas_admin_role
-- ----------------------------
CREATE TABLE IF NOT EXISTS `cas_admin_role` (
  `admin_id` int NOT NULL COMMENT '管理员用户id',
  `role_id` int NOT NULL COMMENT '角色id',
  INDEX `admin_id`(`admin_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '管理员-角色关联表' ROW_FORMAT = Dynamic;
