alter table `cas_user`
    add column `health_coin` int NOT NULL DEFAULT 0 COMMENT '用户总健康币数量' after `gold_coin`;