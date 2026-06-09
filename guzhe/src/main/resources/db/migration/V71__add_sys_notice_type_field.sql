ALTER TABLE `sys_notice`
    ADD COLUMN `type` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '类型：1通知公告 2活动推广'
        AFTER `content`;