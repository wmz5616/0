CREATE TABLE IF NOT EXISTS `reconciliation_transaction_summary` (
                                                                    `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                                                                    `shop_id` int NOT NULL DEFAULT 0 COMMENT '店铺ID',
                                                                    `bill_date` date NOT NULL COMMENT '账单日期',
                                                                    `total_income` bigint NOT NULL DEFAULT 0 COMMENT '交易收入总额(分)',
                                                                    `total_refund` bigint NOT NULL DEFAULT 0 COMMENT '退款支出总额(分)',
                                                                    `fee_amount` bigint NOT NULL DEFAULT 0 COMMENT '手续费支出总额(分)',
                                                                    `fee_return_amount` bigint NOT NULL DEFAULT 0 COMMENT '手续费退回总额(分)',
                                                                    `income_count` int NOT NULL DEFAULT 0 COMMENT '收款笔数',
                                                                    `refund_count` int NOT NULL DEFAULT 0 COMMENT '退款笔数',
                                                                    `total_count` int NOT NULL DEFAULT 0 COMMENT '总笔数',
                                                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                                    PRIMARY KEY (`id`),
                                                                    UNIQUE KEY `uk_date_shop` (`bill_date`, `shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='交易汇总表';