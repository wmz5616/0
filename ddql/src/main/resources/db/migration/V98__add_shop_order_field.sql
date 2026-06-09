ALTER TABLE `shop_order`
    ADD COLUMN `up_order_id` varchar(64) DEFAULT NULL COMMENT '通莞支付订单号' AFTER `divide_time`,
    ADD COLUMN `pay_time` datetime DEFAULT NULL COMMENT '支付成功时间' AFTER `up_order_id`;