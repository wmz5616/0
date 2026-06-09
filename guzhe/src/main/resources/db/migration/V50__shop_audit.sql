-- V50__shop_audit.sql

-- 店铺审核主表
CREATE TABLE IF NOT EXISTS `shop_audit` (
                                            `id` int NOT NULL AUTO_INCREMENT,
                                            `user_id` int NOT NULL COMMENT '申请用户ID',
                                            `cover_image_url` varchar(255) DEFAULT NULL COMMENT '店铺封面图URL',
                                            `gallery_images` text COMMENT '店铺轮播图URL (JSON格式)',
                                            `name` varchar(255) NOT NULL COMMENT '店铺名称',
                                            `location` varchar(255) NOT NULL COMMENT '店铺经纬度',
                                            `address` varchar(255) DEFAULT NULL COMMENT '店铺详细地址',
                                            `user_name` varchar(255) DEFAULT NULL COMMENT '店铺联系人',
                                            `phone` varchar(255) DEFAULT NULL COMMENT '店铺联系电话',
                                            `start_time` varchar(255) DEFAULT NULL COMMENT '营业开始时间, HH:mm:ss',
                                            `end_time` varchar(255) DEFAULT NULL COMMENT '营业结束时间, HH:mm:ss',
                                            `customer_phone` varchar(255) DEFAULT NULL COMMENT '客服电话',
                                            `customer_code_img` varchar(255) DEFAULT NULL COMMENT '客服微信二维码图片',
                                            `description` text NOT NULL COMMENT '店铺介绍（富文本内容）',
                                            `submit_time` datetime NOT NULL COMMENT '提交时间',
                                            `audit_status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态 0待审核 1已通过 2已驳回',
                                            `audit_user` int DEFAULT NULL COMMENT '审核人ID',
                                            `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
                                            `audit_phone` varchar(255) DEFAULT NULL COMMENT '审核人电话',
                                            `reject_reason` varchar(500) DEFAULT NULL COMMENT '驳回原因',
                                            `shop_id` int DEFAULT NULL COMMENT '审核通过后生成的店铺ID',
                                            `create_time` datetime NOT NULL COMMENT '创建时间',
                                            `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                            PRIMARY KEY (`id`),
                                            KEY `idx_user_id` (`user_id`),
                                            KEY `idx_audit_status` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='店铺入驻审核表';
