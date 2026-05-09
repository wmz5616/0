alter table `sys_notice`
    ADD COLUMN `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否 1-是' AFTER `is_publish`;