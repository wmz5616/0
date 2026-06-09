-- ----------------------------
-- Table structure for cas_role_rule
-- ----------------------------
CREATE TABLE IF NOT EXISTS `cas_role_rule` (
  `role_id` int NOT NULL COMMENT '角色id',
  `rule_id` int NOT NULL COMMENT '菜单id',
  INDEX `role_id`(`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色-菜单关联表' ROW_FORMAT = Dynamic;
