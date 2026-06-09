alter table `sys_article`
    add column `sort` int not null default 0 comment '排序' after `status`;