-- ----------------------------
-- Table structure for cas_rule
-- ----------------------------
CREATE TABLE IF NOT EXISTS `cas_rule` (
  `id` int NOT NULL AUTO_INCREMENT,
  `is_menu` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否是菜单：0否，1是',
  `parent_id` int NOT NULL COMMENT '上级菜单id',
  `rule_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `api` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `sort` int NOT NULL DEFAULT 1 COMMENT '排序顺序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;
