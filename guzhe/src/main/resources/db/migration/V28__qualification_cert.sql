CREATE TABLE IF NOT EXISTS `qualification_cert` (
    `id` int NOT NULL AUTO_INCREMENT,
    `business_license` varchar(255) NOT NULL COMMENT '营业执照',
    `other_file` varchar(512) NULL COMMENT '其他附件',
    `phone` varchar(255) NOT NULL COMMENT '联系电话',
    `email` varchar(255) NOT NULL COMMENT '联系邮箱',
    `submit_time` datetime NOT NULL COMMENT '申请时间',
    `cert_result` tinyint NOT NULL COMMENT '认证结果 0未审核 1通过 2驳回',
    `reject_reason` varchar(255) NULL COMMENT '驳回原因',
    `cert_user` int NULL COMMENT '审核人(id)',
    `user_id` int NOT NULL COMMENT '申请人id',
    `shop_id` int NOT NULL COMMENT '对应的商家id',
    `cert_side` tinyint NOT NULL COMMENT '哪个端上填写的 1后台 2小程序',
    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    primary key (id)
) COMMENT '商家-资质认证表' ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
