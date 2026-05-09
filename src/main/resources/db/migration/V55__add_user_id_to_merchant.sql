alter table `merchant`
    add column `user_id` int NOT NULL DEFAULT 0 COMMENT '小程序端创建用户id' after `status`;