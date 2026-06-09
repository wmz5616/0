ALTER TABLE `screen_rental_order`
    ADD COLUMN `display_type` tinyint NOT NULL DEFAULT 0 COMMENT '展示内容类型：1-商品 2-海报' AFTER `nick_name`,
    ADD COLUMN `display_target_id` int NOT NULL DEFAULT 0 COMMENT '展示内容ID' AFTER `display_type`;
