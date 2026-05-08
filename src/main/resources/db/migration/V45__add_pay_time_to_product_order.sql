alter table `product_order`
    add column `pay_time` timestamp(0) NULL DEFAULT NULL COMMENT '支付时间' after `status`;