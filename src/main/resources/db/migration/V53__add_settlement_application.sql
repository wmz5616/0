ALTER TABLE `settlement_application`
    ADD COLUMN `shop_audit_id` int DEFAULT NULL COMMENT '店铺入驻审核id' AFTER `shop_id`;