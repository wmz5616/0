alter table `cas_user_check_in_record`
    add column `equipment_id` int NOT NULL DEFAULT 0 COMMENT '设备id，扫码打卡时才有' after `check_in_method`;