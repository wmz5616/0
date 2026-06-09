-- ----------------------------
-- Table structure for exchange_order
-- ----------------------------
CREATE TABLE IF NOT EXISTS `exchange_order` (
                         `id` int NOT NULL AUTO_INCREMENT COMMENT '订单id',
                         `order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单编号',
                         `user_id` int NOT NULL DEFAULT 0 COMMENT '下单用户id',
                         `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '下单手机号',
                         `nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '下单用户昵称',
                         `product_id` int NOT NULL DEFAULT 0 COMMENT '商品id',
                         `product_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品编号',
                         `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品封面图',
                         `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品名称',
                         `specification` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL DEFAULT '' COMMENT '规格',
                         `unit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '单位',
                         `is_virtual` tinyint NOT NULL DEFAULT '0' COMMENT '是否是虚拟商品（0:否, 1:是）',
                         `single_amount` int NOT NULL DEFAULT 0 COMMENT '单价（金币）',
                         `num` int NOT NULL DEFAULT 0 COMMENT '兑换数量',
                         `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL DEFAULT '' COMMENT '备注',
                         `amount` int NOT NULL DEFAULT 0 COMMENT '支付总金额（金币）',
                         `status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态: 1已完成、2退货中、3已退货',
                         `express_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '快递单号',
                         `express_status` tinyint NOT NULL DEFAULT -2 COMMENT '物流状态：-2--无，-1--待发货，0--在途，1--揽件，2--疑难，3--签收，4--退签，5--派件，6--退回，10--待清关，11--清关中，12--已清关，13--清关异常，14--收件人拒签',
                         `refund_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '退货申请原因',
                         `refund_apply_time` timestamp(0) NULL DEFAULT NULL COMMENT '退货申请时间',
                         `refund_img` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '退货申请图片，多个用英文逗号隔开',
                         `refund_amount` int NOT NULL DEFAULT 0 COMMENT '退货金额(金币)',
                         `refund_time` timestamp(0) NULL DEFAULT NULL COMMENT '退货时间',
                         `refund_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '退货说明',
                         `create_time` datetime NOT NULL COMMENT '创建时间',
                         `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='用户商品兑换订单管理表';