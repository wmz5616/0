alter table `shop`
    add column `customer_phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '客服电话' after `contract`,
    add column `customer_code_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '客服微信二维码图片' after `customer_phone`;