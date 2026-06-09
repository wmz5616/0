-- 获取团体部门列表
INSERT INTO `cas_rule` (`is_menu`, `parent_id`, `rule_name`, `api`, `sort`) VALUES (0, 2, '获取团体部门列表', '/team/get/dept', 9);

-- 新增团体用户
INSERT INTO `cas_rule` (`is_menu`, `parent_id`, `rule_name`, `api`, `sort`) VALUES (0, 2, '新增团体用户', '/team/user/add', 10);

-- 部门管理相关接口
INSERT INTO `cas_rule` (`is_menu`, `parent_id`, `rule_name`, `api`, `sort`) VALUES (0, 2, '新增团体部门', '/team/department/add', 11);
INSERT INTO `cas_rule` (`is_menu`, `parent_id`, `rule_name`, `api`, `sort`) VALUES (0, 2, '编辑团体部门', '/team/department/update', 12);
INSERT INTO `cas_rule` (`is_menu`, `parent_id`, `rule_name`, `api`, `sort`) VALUES (0, 2, '删除团体部门', '/team/department/delete', 13);
