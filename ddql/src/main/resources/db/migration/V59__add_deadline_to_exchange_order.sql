alter table `exchange_order`
    add column `deadline` timestamp(0) NULL DEFAULT NULL COMMENT '有效截止时间，虚拟商品才有' after `is_virtual`;