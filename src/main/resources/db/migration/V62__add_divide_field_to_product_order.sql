alter table `product_order`
    add column `divide_status` tinyint NOT NULL DEFAULT 0 COMMENT '分账状态：0未分账，1已分账' after `express_status`,
    add column `divide_amount` int NOT NULL DEFAULT 0 COMMENT '分账金额（分）' after `divide_status`,
    add column `divide_time` datetime(0) NULL DEFAULT NULL COMMENT '分账时间' after `divide_amount`;