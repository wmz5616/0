alter table `team_user`
    add column `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
    add UNIQUE INDEX `idx_team_user`(`team_id`,`user_id`) USING BTREE;