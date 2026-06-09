-- 团队多部门管理功能

-- 1. 新增部门表
CREATE TABLE IF NOT EXISTS `team_department` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `team_id` INT NOT NULL COMMENT '所属团队ID',
    `name` VARCHAR(50) NOT NULL COMMENT '部门名称',
    `sort` INT DEFAULT 0 COMMENT '排序，越小越靠前',
    `status` TINYINT DEFAULT 0 COMMENT '状态：1启用，0禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_time` DATETIME NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队部门表';

-- 2. 团队表增加是否多部门管理字段
ALTER TABLE `team` 
ADD COLUMN `is_multi_department` TINYINT NOT NULL DEFAULT 0 COMMENT '是否多部门管理：0否，1是' AFTER `is_user_auth`;

-- 3. 团队成员表增加部门ID字段
ALTER TABLE `team_user` 
ADD COLUMN `department_id` INT NULL DEFAULT NULL COMMENT '所属部门ID' AFTER `type`;

-- 申请表增加部门ID字段
ALTER TABLE `team_application_record`
ADD COLUMN `department_id` INT NULL DEFAULT NULL COMMENT '所属部门ID' AFTER `team_id`;


