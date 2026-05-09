-- ----------------------------
-- Table structure for cas_admin
-- ----------------------------
CREATE TABLE IF NOT EXISTS `cas_admin` (
  `id` int NOT NULL AUTO_INCREMENT,
  `account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '账号(手机号)',
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '密码',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '名称',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态：0禁用、1启用',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '管理员管理表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Records of cas_admin
-- ----------------------------
INSERT INTO `cas_admin` VALUES (1, '18566568198', 'e498d5ef120cda7014831716b637aaf3', 'admin', 1, '', '2025-05-06 00:00:00', '2025-05-06 00:00:00', null);
