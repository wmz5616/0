-- 为 sys_article 表添加排序字段
ALTER TABLE `sys_article`
    ADD COLUMN `sort` int unsigned DEFAULT 0 COMMENT '排序值' AFTER `status`;
