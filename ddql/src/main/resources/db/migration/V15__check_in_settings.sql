
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
CREATE TABLE IF NOT EXISTS `check_in_settings` (
                                     `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
                                     `target_steps` int unsigned DEFAULT NULL COMMENT '目标步数',
                                     `steps_gold_coin` int DEFAULT NULL COMMENT '步数打卡发放的金币数量',
                                     `scan_code_gold_coin` int unsigned DEFAULT NULL COMMENT '扫码打卡发放的金币数量',
                                     `check_in_instruction` text COLLATE utf8mb4_general_ci COMMENT '打卡说明',
                                     `withdrawal_instruction` text COLLATE utf8mb4_general_ci COMMENT '提现说明',
                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统打卡设置';

SET FOREIGN_KEY_CHECKS = 1;

-- 初始数据
INSERT INTO `check_in_settings` VALUES (1, 10000, 10, 10, '打卡说明', '提现说明')