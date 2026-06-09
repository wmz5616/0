alter table `product`
    add column `time_limit` int NOT NULL DEFAULT 0 COMMENT '有效期（天），虚拟商品才有' after `is_virtual`;