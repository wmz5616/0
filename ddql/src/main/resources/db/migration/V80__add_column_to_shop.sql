ALTER TABLE `shop`
ADD column `qualification_cert` tinyint NOT NULL DEFAULT 0 COMMENT '资质认证 0未认证 1待审核 2已通过 3已驳回' AFTER `remark`,
ADD column `shop_status` tinyint NOT NULL DEFAULT 0 COMMENT '商家状态 0正常 1禁用 2已注销' AFTER `status`,
ADD column `receipt_status` tinyint NOT NULL DEFAULT 0 COMMENT '收款启用状态 0关闭收款 1开启收款' AFTER `shop_status`,
ADD column `top_consumption` tinyint NOT NULL DEFAULT 0 COMMENT '消费置顶 0否 1是' AFTER `remark`,
ADD column `top_consumption_start_time` datetime DEFAULT NULL COMMENT '消费置顶开始时间' AFTER `top_consumption`,
ADD column `top_consumption_end_time` datetime DEFAULT NULL COMMENT '消费置顶结束时间' AFTER `top_consumption`,
ADD column `delete_time` datetime DEFAULT NULL COMMENT '删除时间' AFTER `update_time`,
ADD column `rate` int NOT NULL DEFAULT 0 COMMENT '单位 百分之一 费率' AFTER `top_consumption_start_time`,
ADD column `contract` varchar(255) DEFAULT NULL COMMENT '合同图片' AFTER `rate`;

