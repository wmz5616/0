-- 为 shop_audit 表添加店铺启用状态和推荐排序字段
ALTER TABLE `shop_audit`
    ADD COLUMN `status` int NOT NULL DEFAULT 0 COMMENT '店铺启用状态 0禁用 1启用' AFTER `qualification_cert`,
    ADD COLUMN `recommend_order` int DEFAULT 0 COMMENT '推荐顺序' AFTER `status`;
