ALTER TABLE `shop`
    ADD COLUMN `business_time` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '营业时间' AFTER `end_time`;

UPDATE `shop`
SET `business_time` = CONCAT_WS(' - ', NULLIF(`start_time`, ''), NULLIF(`end_time`, ''))
WHERE (`business_time` IS NULL OR `business_time` = '')
  AND ((`start_time` IS NOT NULL AND `start_time` <> '') OR (`end_time` IS NOT NULL AND `end_time` <> ''));