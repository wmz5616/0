alter table `team`
add column `is_user_auth` tinyint NOT NULL DEFAULT 1 COMMENT '进团是否审核：0否，1是' after `status`;