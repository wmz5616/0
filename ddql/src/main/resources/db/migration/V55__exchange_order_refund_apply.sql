-- ----------------------------
-- Table structure for exchange_order_refund_apply
-- ----------------------------
CREATE TABLE IF NOT EXISTS `exchange_order_refund_apply` (
                             `id` int NOT NULL AUTO_INCREMENT,
                             `order_id` bigint NOT NULL COMMENT '订单id',
                             `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '退货申请原因',
                             `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '退货申请图片，多个用英文逗号隔开',
                             `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态: 1审核中、2审核通过、3审核驳回',
                             `admin_id` int NOT NULL DEFAULT 0 COMMENT '审核管理员id',
                             `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '审核管理员账号',
                             `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '审核管理员姓名',
                             `refund_amount` int NOT NULL DEFAULT 0 COMMENT '退货金额(金币)',
                             `audit_time` timestamp(0) NULL DEFAULT NULL COMMENT '审核时间',
                             `audit_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '审核说明',
                             `create_time` datetime NOT NULL COMMENT '创建时间',
                             `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `order_id`(`order_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='用户商品兑换订单退款申请管理表';