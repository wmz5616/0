-- 为资质认证表添加审核记录ID字段，用于关联商家审核表
ALTER TABLE `qualification_cert`
    ADD COLUMN `shop_audit_id` INT NULL COMMENT '商家审核记录ID（关联shop_audit表）' AFTER `shop_id`;