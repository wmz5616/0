ALTER TABLE `exchange_order`
    ADD COLUMN `pay_way` tinyint NOT NULL DEFAULT 0 COMMENT '支付方式 0金币 1组合 2现金' AFTER `remark`,
    ADD COLUMN `cash_amount` int NOT NULL DEFAULT 0 COMMENT '现金支付金额（单位：分）' AFTER `amount`,
    ADD COLUMN `up_order_id` varchar(64) DEFAULT NULL COMMENT '通莞支付订单号' AFTER `cash_amount`,
    ADD COLUMN `pay_time` datetime DEFAULT NULL COMMENT '支付成功时间' AFTER `up_order_id`,
    ADD COLUMN `refund_cash_amount` int NOT NULL DEFAULT 0 COMMENT '现金退款金额（单位：分）' AFTER `refund_amount`,
    ADD KEY `idx_up_order_id` (`up_order_id`);

ALTER TABLE `exchange_order`
    MODIFY COLUMN `status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态: 0待支付、1待使用(虚拟商品)、2待发货(非虚拟商品)、3已发货(非虚拟商品)、4已完成、5退款中、6已退款、7已过期(虚拟商品)、8已取消';
