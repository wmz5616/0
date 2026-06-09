-- 1. 补齐 merchant 表缺失的 user_id 字段
ALTER TABLE `merchant` ADD COLUMN `user_id` int NOT NULL DEFAULT 0 COMMENT '小程序端创建用户id' AFTER `status`;

-- 2. 补齐 sys_config 表商户入驻协议配置
INSERT INTO `sys_config` (`id`, `key`, `value`, `remark`, `type`, `create_time`, `update_time`) VALUES (10, 'show_merchant_notice', '0', '是否展示商户入驻须知：0-否，1-是', 1, NOW(), NOW());
INSERT INTO `sys_config` (`id`, `key`, `value`, `remark`, `type`, `create_time`, `update_time`) VALUES (11, 'merchant_notice', '', '商户入驻须知内容（支持多图）', 1, NOW(), NOW());