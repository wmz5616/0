alter table `home_page_banner`
add column `sort` int not null default 0 comment '排序' after `status`;