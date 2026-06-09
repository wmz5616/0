alter table `recharge_order`
    add column `refund_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '退款订单号' after `pay_time`;