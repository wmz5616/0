-- ----------------------------
-- Table structure for express_order
-- ----------------------------
CREATE TABLE IF NOT EXISTS `express_order`  (
                               `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
                               `txn_type` tinyint NOT NULL DEFAULT 0 COMMENT '关联的系统订单类型：1商品兑换订单',
                               `txn_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '对应的系统记录id',
                               `express_company_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '快递公司名称',
                               `express_company_code` char(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '快递公司标识码',
                               `express_no` char(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '运单号',
                               `info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '物流详情，json格式',
                               `status` tinyint NOT NULL DEFAULT -1 COMMENT '快递状态：-1--待发货，0--在途，1--揽件，2--疑难，3--签收，4--退签，5--派件，6--退回，10--待清关，11--清关中，12--已清关，13--清关异常，14--收件人拒签',
                               `is_record` tinyint NOT NULL DEFAULT 1 COMMENT '是否为被系统记录关联着，0否，1是（0时代表该单跟踪中途，对应的记录被切换到了新的物流，但是快递100那边无法停止订阅，估需要该字段判断）',
                               `is_sub` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否已订阅，0否，1是',
                               `create_time` datetime NOT NULL COMMENT '创建时间',
                               `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `express_company_code`(`express_company_code`) USING BTREE,
                               INDEX `express_no`(`express_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '物流订单管理表' ROW_FORMAT = Dynamic;