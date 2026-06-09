-- ----------------------------
-- Table structure for sys_pay_log
-- ----------------------------
CREATE TABLE `sys_pay_log`  (
                            `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                            `order_type` tinyint UNSIGNED NOT NULL COMMENT '订单类型',
                            `order_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '对应类型的系统订单id',
                            `user_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户id',
                            `out_trade_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '系统生成的订单号',
                            `wx_transaction_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '支付交易单号',
                            `wx_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '支付回调结果信息（json格式）',
                            `total_fee` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '支付金额(分)',
                            `status` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1支付成功，2支付失败，3已退款，4退款失败',
                            `refund_trade_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '系统生成的退款订单号',
                            `refund_price` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '退款金额(分)',
                            `refund_time` timestamp(0) NULL DEFAULT NULL COMMENT '退款时间',
                            `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
                            `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
                            `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付日志管理表' ROW_FORMAT = DYNAMIC;

