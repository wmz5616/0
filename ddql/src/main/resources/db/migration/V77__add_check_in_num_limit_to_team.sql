alter table `team`
    add column `check_in_num_limit` int not null default 0 comment '每月打卡次数要求，0为无' after `is_user_auth`;