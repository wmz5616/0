SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `team_check_in_settings`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `team_id` int UNSIGNED NOT NULL COMMENT '关联的团队id',
  `scan_code_time` int UNSIGNED NOT NULL COMMENT '扫码打卡时长（分钟）',
  `scan_code_healthy_coin` int NOT NULL COMMENT '扫码打卡可得健康币数量',
  `steps_open` int UNSIGNED NOT NULL COMMENT '是否开启步数打卡 0开启 1关闭',
  `target_steps` int UNSIGNED NULL DEFAULT 0 COMMENT '步数打卡的目标步数',
  `steps_healthy_coin` int UNSIGNED NULL DEFAULT 0 COMMENT '步数打卡可得健康币数量',
  `lowest_withdrawal_money` int UNSIGNED NOT NULL COMMENT '最低提现金额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '团队打卡提现设置' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
