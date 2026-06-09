alter table `shop`
    add column `click_count` int not null default 0 comment '点击次数' after `remark`;