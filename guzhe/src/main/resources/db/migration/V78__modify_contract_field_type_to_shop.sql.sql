ALTER TABLE `shop`
    MODIFY COLUMN `contract` text COLLATE utf8mb4_general_ci NULL COMMENT '合同图片，多个英文逗号分隔';