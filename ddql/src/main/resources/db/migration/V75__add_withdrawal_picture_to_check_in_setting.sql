alter table `check_in_settings`
    add column `withdrawal_picture` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '提现分享海报图，json格式';