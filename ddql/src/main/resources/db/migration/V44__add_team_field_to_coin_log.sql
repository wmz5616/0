alter table `cas_user_coin_log`
    add column `team_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '团队名字' after `team_id`,
    add column `team_type` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '团队类型 0企事单位 1政府部分 2家庭 3朋友' after `team_name`;