alter table `product`
    add column `exchange_num` int NOT NULL DEFAULT 0 COMMENT '兑换数量' after `remark`;