alter table `shop`
    add column `merchant_id` int NULL DEFAULT NULL COMMENT '绑定的商户id' AFTER `remark`;