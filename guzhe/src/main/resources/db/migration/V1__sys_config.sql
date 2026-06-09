-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_config`  (
                                  `id` int NOT NULL AUTO_INCREMENT,
                                  `key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配置key',
                                  `value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '配置value',
                                  `remark`varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配置备注说明',
                                  `type` tinyint NOT NULL DEFAULT 1 COMMENT '配置类型',
                                  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
                                  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '配置管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 'logo', '', '系统logo', 1, '2024-07-09 00:00:00', '2024-07-23 22:21:16');
INSERT INTO `sys_config` VALUES (2, 'name', '', '系统名称', 1, '2024-07-09 00:00:00', '2024-07-24 17:25:31');
INSERT INTO `sys_config` VALUES (3, 'version', '', '版权信息', 1, '2024-07-09 00:00:00', '2024-07-23 22:21:16');
INSERT INTO `sys_config` VALUES (4, 'miitbeian', '', '备案信息', 1, '2024-07-09 00:00:00', '2024-07-22 15:53:09');
INSERT INTO `sys_config` VALUES (5, 'login_page_pic', '', '登录页面配图', 1, '2024-07-09 00:00:00', '2024-07-09 09:28:27');
INSERT INTO `sys_config` VALUES (6, 'org_name', '', '单位名称', 1, '2024-07-09 00:00:00', '2024-07-09 09:28:27');
INSERT INTO `sys_config` VALUES (7, 'address', '', '联系地址', 1, '2024-07-09 00:00:00', '2024-07-09 09:28:27');
INSERT INTO `sys_config` VALUES (8, 'phone', '', '联系电话', 1, '2024-07-09 00:00:00', '2024-07-09 09:28:27');
INSERT INTO `sys_config` VALUES (9, 'email', '', '联系邮箱', 1, '2024-07-09 00:00:00', '2024-07-09 09:28:27');


INSERT INTO `sys_config` VALUES (10, 'show_rental_contract', '', '是否展示屏幕店租用合约：0-否，1-是', 2, NOW(), NOW());
INSERT INTO `sys_config` VALUES (11, 'rental_contract', '', '屏幕店租用合约内容（富文本）', 2, NOW(), NOW());
INSERT INTO `sys_config` VALUES (12, 'introduce', '', '平台介绍', 1, '2024-07-09 00:00:00', '2024-07-09 09:28:27');