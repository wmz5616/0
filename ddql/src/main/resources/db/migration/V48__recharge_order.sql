-- ----------------------------
-- Table structure for recharge_order
-- ----------------------------
CREATE TABLE IF NOT EXISTS `recharge_order` (
                         `id` int NOT NULL AUTO_INCREMENT COMMENT '订单id',
                         `order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单编号',
                         `wx_transaction_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '微信交易订单号',
                         `user_id` int NOT NULL DEFAULT 0 COMMENT '下单用户id',
                         `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '下单手机号',
                         `nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '下单用户昵称',
                         `team_id` int NOT NULL DEFAULT 0 COMMENT '团队id',
                         `team_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '团队名字',
                         `team_type` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '团队类型 0企事单位 1政府部分 2家庭 3朋友',
                         `act_id` int NOT NULL DEFAULT 0 COMMENT '充值活动id',
                         `give_amount` int NOT NULL DEFAULT 0 COMMENT '赠送金额 单位为分',
                         `amount` int NOT NULL DEFAULT 0 COMMENT '支付总金额（充值金额） 单位为分',
                         `pay_type` tinyint NOT NULL DEFAULT 0 COMMENT '支付方式 1微信支付',
                         `status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态: 0无、1待支付、2已支付、3已取消、4已退款',
                         `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
                         `refund_amount` int NOT NULL DEFAULT 0 COMMENT '退款金额，单位为分',
                         `refund_time` timestamp(0) NULL DEFAULT NULL COMMENT '退款时间',
                         `refund_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '退款说明',
                         `create_time` datetime NOT NULL COMMENT '创建时间',
                         `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                         PRIMARY KEY (`id`) USING BTREE,
                         INDEX `team_id`(`team_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='充值购币订单管理表';