alter table `daily_visit_trend`
    add column `year` int not null default 0 COMMENT '年份' after `id`,
    add column `month` char(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '月份' after `year`,
    add index `idx_year`(`year`),
    add index `idx_month`(`month`);