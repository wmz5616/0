-- 规格类型表和规格值表新增排序字段

-- 1. 规格类型表新增排序字段
ALTER TABLE `product_spec_type`
ADD COLUMN `sort` INT DEFAULT 0 COMMENT '排序' AFTER `type_name`;

-- 2. 规格值表新增排序字段
ALTER TABLE `product_spec_value`
ADD COLUMN `sort` INT DEFAULT 0 COMMENT '排序' AFTER `value_name`;
