alter table `check_in_type`
    add column `sort` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '排序值，升序';