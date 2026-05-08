
CREATE TABLE IF NOT EXISTS `settlement_application`(
                                                       `id` int NOT NULL AUTO_INCREMENT,
                                                       `user_id` int NOT NULL COMMENT '申请用户id',
                                                       `phone` varchar(255) NOT NULL COMMENT '申请用户电话',
                                                       `submit_time` datetime NOT NULL COMMENT '申请时间',
                                                       `apply_result` tinyint NOT NULL DEFAULT '0' COMMENT '申请状态 0待审核 1已通过 2已驳回',
                                                       `reject_reason` varchar(255) DEFAULT NULL COMMENT '驳回原因',
                                                       `audit_user` int DEFAULT NULL COMMENT '审核人id',
                                                       `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
                                                       `audit_phone` varchar(255) DEFAULT NULL COMMENT '审核人电话',
                                                       `shop_id` int DEFAULT NULL COMMENT '店铺id',
                                                       primary key (`id`)
)COMMENT '申请入驻表'