ALTER TABLE `product_category`
    ADD COLUMN `shop_id` int NOT NULL DEFAULT 0 COMMENT '商家id' AFTER `id`;