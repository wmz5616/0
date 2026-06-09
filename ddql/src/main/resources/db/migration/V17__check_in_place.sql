SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT  EXISTS `check_in_place`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '场所名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '场所地址',
  `check_in_method` int UNSIGNED NOT NULL COMMENT '打卡方式 0扫码打卡 1距离打卡',
  `check_in_distance` int NULL DEFAULT NULL COMMENT '打卡距离 单位米',
  `check_in_type_id` int NOT NULL COMMENT '关联的打卡类型id',
  `contact_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `status` int NOT NULL DEFAULT 0 COMMENT '启用状态 0开启 1关闭 默认为开启',
  `sort` int UNSIGNED NULL DEFAULT 0 COMMENT '默认为 0 数值大的在前',
  `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '场地介绍',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '图片的url集合 以;分隔',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '打卡场所' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
