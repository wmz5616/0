alter table `product_order_refund_apply`
    add column `old_status` tinyint NOT NULL DEFAULT 0 COMMENT '申请时订单状态' after `order_id`;