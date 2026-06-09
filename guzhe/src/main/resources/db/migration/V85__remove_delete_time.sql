-- 移除规格类型表和规格值表的delete_time字段，修正规格价格库存表的delete_time默认值

-- 1. 移除规格类型表的delete_time字段
ALTER TABLE `product_spec_type` 
DROP COLUMN `delete_time`;

-- 2. 移除规格值表的delete_time字段
ALTER TABLE `product_spec_value` 
DROP COLUMN `delete_time`;

-- 3. 修正规格价格库存表的delete_time默认值（改为NULL）
ALTER TABLE `product_spec_price` 
MODIFY COLUMN `delete_time` DATETIME NULL COMMENT '删除时间';