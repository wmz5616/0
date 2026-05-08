CREATE TABLE IF NOT EXISTS `home_banner` (
                               `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                               `ban_url` VARCHAR(500) NOT NULL COMMENT '图片URL地址',
                               `type` TINYINT DEFAULT 0 COMMENT '链接类型：0-小程序页面，1-H5页面',
                               `url` VARCHAR(500) DEFAULT NULL COMMENT '跳转链接的URL',
                               `status` TINYINT DEFAULT 1 COMMENT '是否启用：0-不启用，1-启用（默认）',
                               `sort` INT DEFAULT 0 COMMENT '排序字段，数值越大越靠前',
                               `name` VARCHAR(100) DEFAULT NULL COMMENT '名称',
                               `nick_name` VARCHAR(100) DEFAULT NULL COMMENT '别名',
                               `sort_type` INT DEFAULT NULL COMMENT '权限控制类型',
                               `banner_type` TINYINT DEFAULT 0 COMMENT '展示类型：0-首页banner图，1-首页快捷入口',
                               `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页轮播图表';
