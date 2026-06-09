alter table `team_application_record`
add column `join_type` int not null default 0 comment '加入的方式 0申请加入 1扫码加入' after `status`;