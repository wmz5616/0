alter table `equipment`
    add column `version_id` int NOT NULL DEFAULT 0 COMMENT '当前版本id' after `remark`,
    add column `version_serial_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '版本序列号' after `version_id`,
    add column `release` int NOT NULL DEFAULT 0 COMMENT '版本release' after `version_serial_number`,
    add column `app_require_time` datetime(0) NULL DEFAULT NULL COMMENT '设备最近一次请求时间，主要用于检测设备是否离线' after `release`;