CREATE TABLE `home_page_banner` (
                                    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `pic` VARCHAR(500) NOT NULL COMMENT '图片的URL',
                                    `type` TINYINT NOT NULL DEFAULT 0 COMMENT '链接类型：0-小程序页面 1-H5页面',
                                    `url` VARCHAR(500) NOT NULL COMMENT '跳转链接的URL',
                                    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0-不启用 1-启用',
                                    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序字段',
                                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播图表';
