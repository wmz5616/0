CREATE TABLE `reconciliation_subledger_summary` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `shop_id` int NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `bill_date` date NOT NULL COMMENT '账单日期',
  `total_divide_amount` bigint NOT NULL DEFAULT '0' COMMENT '分账总金额（分）',
  `total_handling_charge` bigint NOT NULL DEFAULT '0' COMMENT '第三方手续费总额(分)',
  `total_platform_charge` bigint NOT NULL DEFAULT '0' COMMENT '平台收费总额(分)',
  `total_count` int NOT NULL DEFAULT '0' COMMENT '总笔数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_shop` (`bill_date`,`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='分账汇总表';
