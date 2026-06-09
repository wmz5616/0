alter table `sys_notice`
    add column `is_top` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否置顶 0否 1是' after `is_publish`;