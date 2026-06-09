SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `equipment_poster`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `equipment_id` int UNSIGNED NOT NULL COMMENT '关联的设备id',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '图片的url',
  `status` int NOT NULL COMMENT '0待投放 1正常 2已过期',
  `show_time` int NOT NULL COMMENT '展示时间 单位秒',
  `show_begin_time` datetime NOT NULL COMMENT '投放的开始时间',
  `show_end_time` datetime NOT NULL COMMENT '投放的结束时间',
  `sort` int UNSIGNED NOT NULL COMMENT '排序字段 ',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '设备-海报关联表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
