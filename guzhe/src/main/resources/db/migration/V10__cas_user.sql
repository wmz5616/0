-- ----------------------------
-- Table structure for cas_user
-- ----------------------------
CREATE TABLE IF NOT EXISTS `cas_user`  (
                                 `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                                 `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
                                 `phone` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '手机号',
                                 `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '头像',
                                 `sex` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-未知 1-男 2-女',
                                 `open_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信openid',
                                 `union_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信unionid',
                                 `has_certification` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已实名认证：0否，1是',
                                 `card_num` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '身份证号码',
                                 `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '姓名',
                                 `birth_date` date NULL DEFAULT NULL COMMENT '生日',
                                 `area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '籍贯',
                                 `gold_coin` int NULL DEFAULT 0 COMMENT '金币数量',
                                 `lock` tinyint(1) NOT NULL DEFAULT 0 COMMENT '用户锁 0正常 1暂时锁定 2永久锁定',
                                 `lock_expired_at` timestamp(0) NULL DEFAULT NULL COMMENT '锁有效期',
                                 `lock_reason` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '锁定原因',
                                 `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '管理员备注',
                                 `admin_id` int NOT NULL DEFAULT 0 COMMENT '管理员id',
                                 `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
                                 `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信用户管理表' ROW_FORMAT = DYNAMIC;

