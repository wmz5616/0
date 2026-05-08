-- V57__add_qualification_cert_to_shop_audit.sql
-- 为 shop_audit 表添加资质认证状态字段

ALTER TABLE `shop_audit`
    ADD COLUMN `qualification_cert` TINYINT NOT NULL DEFAULT 0 COMMENT '资质认证状态 0未提交 1已通过 2已驳回' AFTER `shop_id`;
