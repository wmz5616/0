ALTER TABLE `product`
    ADD column `pay_way` tinyint NOT NULL DEFAULT 0 COMMENT '支付方式 0金币 1组合 2现金' AFTER `detail`,
    ADD column `pay_amount` int DEFAULT NULL COMMENT '支付金额 单位是分' AFTER `pay_way`;

