alter table `order`
    add column `up_order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '通莞支付订单号' after `order_no`;