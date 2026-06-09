CREATE TABLE `team_feedback` (
                                 `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                 `team_id` INT NOT NULL COMMENT '团体ID',
                                 `user_id` INT NOT NULL COMMENT '用户ID',
                                 `user_name` VARCHAR(50) NOT NULL COMMENT '用户昵称（匿名时存储"匿名用户"）',
                                 `content` VARCHAR(50) NOT NULL COMMENT '反馈内容（不超过50字）',
                                 `is_anonymous` TINYINT DEFAULT 0 COMMENT '是否匿名：0-否，1-是',
                                 `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `delete_time` DATETIME DEFAULT NULL COMMENT '删除时间（软删除）',
                                 INDEX `idx_team_id` (`team_id`),
                                 INDEX `idx_user_id` (`user_id`),
                                 INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团体意见反馈表';