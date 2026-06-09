SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


CREATE TABLE IF NOT EXISTS `team_verification`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `team_id` int UNSIGNED NOT NULL COMMENT '团队id',
  `license_type` int NULL DEFAULT NULL COMMENT '证件类型',
  `license_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '营业执照或者法人证书 以;分隔',
  `addition_picture` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '附件 以;分隔',
  `verification_type` int UNSIGNED NULL DEFAULT NULL COMMENT '审核的类型  0 企事业单位, 1 政府部门',
  `type` int NOT NULL COMMENT '审核方式 0正常审核 1人工审核',
  `contact_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '联系电话',
  `contact_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系邮箱',
  `status` int NOT NULL DEFAULT 0 COMMENT '审核状态 0审核中 1审核通过 2审核驳回',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '团队资质审核记录表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
