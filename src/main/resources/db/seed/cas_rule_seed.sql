-- ----------------------------
-- Records of cas_rule
-- ----------------------------
INSERT INTO `cas_rule` VALUES (1, 1, 0, '数据总览', '', 1);
INSERT INTO `cas_rule` VALUES (2, 1, 0, '商家管理', '', 2);
INSERT INTO `cas_rule` VALUES (3, 1, 0, '商超信息管理', '', 3);
INSERT INTO `cas_rule` VALUES (4, 1, 0, '商品订单管理', '', 4);
INSERT INTO `cas_rule` VALUES (5, 1, 0, '终端设备管理', '', 5);
INSERT INTO `cas_rule` VALUES (6, 1, 0, '店位租用管理', '', 6);
INSERT INTO `cas_rule` VALUES (7, 1, 0, '财务管理', '', 7);
INSERT INTO `cas_rule` VALUES (8, 1, 0, '商户管理', '', 8);
INSERT INTO `cas_rule` VALUES (9, 1, 0, '通知公告', '', 9);
INSERT INTO `cas_rule` VALUES (10, 1, 0, '用户管理', '', 10);
INSERT INTO `cas_rule` VALUES (11, 1, 0, '权限管理', '', 11);
INSERT INTO `cas_rule` VALUES (12, 1, 0, '系统设置', '', 12);
INSERT INTO `cas_rule` VALUES (13, 1, 0, 'APP版本控制', '', 13);

-- ----------------------------
INSERT INTO `cas_rule` VALUES (14, 1, 3, '商超信息管理', '', 1);
INSERT INTO `cas_rule` VALUES (15, 0, 14, '查询商超信息列表', '/supermarket/select', 1);
INSERT INTO `cas_rule` VALUES (16, 0, 14, '新增商超信息', '/supermarket/add', 2);
INSERT INTO `cas_rule` VALUES (17, 0, 14, '删除商超信息', '/supermarket/delete', 3);
INSERT INTO `cas_rule` VALUES (18, 0, 14, '编辑商超启用状态', '/supermarket/status/set', 4);
INSERT INTO `cas_rule` VALUES (19, 0, 14, '编辑商超', '/supermarket/update', 5);
-- ----------------------------
INSERT INTO `cas_rule` VALUES (20, 1, 5, '终端设备管理', '/equipment/select', 1);
INSERT INTO `cas_rule` VALUES (21, 0, 20, '新增设备', '/equipment/add', 1);
INSERT INTO `cas_rule` VALUES (22, 0, 20, '修改设备', '/equipment/update', 2);
INSERT INTO `cas_rule` VALUES (23, 0, 20, '查询设备', '/equipment/select', 3);
INSERT INTO `cas_rule` VALUES (24, 0, 20, '删除设备', '/equipment/delete', 4);
INSERT INTO `cas_rule` VALUES (25, 0, 20, '编辑设备状态', '/equipment/status/set', 5);
Insert into `cas_rule` values (26, 0, 20, '获取设备日志列表', '/equipment/log/lists', 6);

-- ----------------------------
INSERT INTO `cas_rule` VALUES (27, 1, 9, '通知公告', '', 1);
INSERT INTO `cas_rule` VALUES (28, 0, 27, '新增通知公告', '/notice/add', 1);
INSERT INTO `cas_rule` VALUES (29, 0, 27, '编辑通知公告', '/notice/update', 2);
INSERT INTO `cas_rule` VALUES (30, 0, 27, '删除通知公告', '/notice/delete', 3);
INSERT INTO `cas_rule` VALUES (31, 0, 27, '获取通知公告详情', '/notice/getInfo', 4);
INSERT INTO `cas_rule` VALUES (32, 0, 27, '获取通知公告列表', '/notice/getNoticeList', 5);
INSERT INTO `cas_rule` VALUES (33, 0, 27, '设置通知公告置顶状态', '/notice/top/set', 6);

-- ----------------------------
INSERT INTO `cas_rule` VALUES (34, 1, 10, '用户管理', '', 1);
INSERT INTO `cas_rule` VALUES (35, 0, 34, '查询用户列表', '/user/lists', 1);
INSERT INTO `cas_rule` VALUES (36, 0, 34, '编辑用户状态', '/user/update', 2);
INSERT INTO `cas_rule` VALUES (37, 0, 34, '导出用户信息', '/user/export', 3);

-- ----------------------------
INSERT INTO `cas_rule` VALUES (38, 1, 12, '用户端设置', '', 1);
INSERT INTO `cas_rule` VALUES (39, 1, 38, '首页轮播图', '', 2);
INSERT INTO `cas_rule` VALUES (40, 0, 39, '新增首页轮播图', '/homePage/banner/insert', 1);
INSERT INTO `cas_rule` VALUES (41, 0, 39, '修改首页轮播图', '/homePage/banner/update', 2);
INSERT INTO `cas_rule` VALUES (42, 0, 39, '删除首页轮播图', '/homePage/banner/delete', 3);
INSERT INTO `cas_rule` VALUES (43, 0, 39, '获取所有轮播图列表', '/homePage/banner/allList', 4);
INSERT INTO `cas_rule` VALUES (44, 0, 39, '获取展示轮播图', '/homePage/banner/showList', 5);
INSERT INTO `cas_rule` VALUES (45, 0, 39, '编辑首页轮播图状态', '/homePage/banner/status/set', 6);
INSERT INTO `cas_rule` VALUES (46, 0, 39, '修改首页轮播图顺序', '/homePage/banner/sort/set', 7);
INSERT INTO `cas_rule` VALUES (47, 1, 38, '首页banner图', '',3 );
INSERT INTO `cas_rule` VALUES (48, 1, 38, '首页快捷入口', '',4 );
INSERT INTO `cas_rule` VALUES (49, 0, 48, '新增首页轮播图', '/homePage/banner/insert', 1);
INSERT INTO `cas_rule` VALUES (50, 0, 48, '修改首页轮播图', '/homePage/banner/update', 2);
INSERT INTO `cas_rule` VALUES (51, 0, 48, '删除首页轮播图', '/homePage/banner/delete', 3);
INSERT INTO `cas_rule` VALUES (52, 0, 48, '获取所有轮播图列表', '/homePage/banner/allList', 4);
INSERT INTO `cas_rule` VALUES (53, 0, 48, '获取展示轮播图', '/homePage/banner/showList', 5);
INSERT INTO `cas_rule` VALUES (54, 0, 48, '编辑首页轮播图状态', '/homePage/banner/status/set', 6);
INSERT INTO `cas_rule` VALUES (55, 0, 48, '修改首页轮播图顺序', '/homePage/banner/sort/set', 7);
INSERT INTO `cas_rule` VALUES (56, 1, 38, '登录页面配图', '', 5);

INSERT INTO `cas_rule` VALUES (57, 1, 12, '系统管理', '', 2);
INSERT INTO `cas_rule` VALUES (58, 0, 57, '新增文章', '/basic/config', 1);
INSERT INTO `cas_rule` VALUES (59, 0, 57, '修改基础配置信息', '/basic/config/update', 2);
INSERT INTO `cas_rule` VALUES (60, 0, 57, '新增文章', '/article/add', 3);
INSERT INTO `cas_rule` VALUES (61, 0, 57, '修改文章', '/article/update', 4);
INSERT INTO `cas_rule` VALUES (62, 0, 57, '删除文章', '/article/delete', 5);
INSERT INTO `cas_rule` VALUES (63, 0, 57, '查询文章', '/article/select', 6);
INSERT INTO `cas_rule` VALUES (64, 0, 57, '更改文章展示顺序', '/article/sort/set', 7);
INSERT INTO `cas_rule` VALUES (65, 0, 57, '获取系统基础配置信息', '/system/basic/config', 8);
INSERT INTO `cas_rule` VALUES (66, 0, 57, '修改基础配置信息', '/system/basic/config/update', 9);

-- ----------------------------
INSERT INTO `cas_rule` VALUES (67, 0, 11, '管理员管理', '', 1);
INSERT INTO `cas_rule` VALUES (68, 0, 67, '新增管理员', '/admin/add', 1);
INSERT INTO `cas_rule` VALUES (69, 0, 67, '编辑管理员', '/admin/update', 2);
INSERT INTO `cas_rule` VALUES (70, 0, 67, '删除管理员', '/admin/delete', 3);
INSERT INTO `cas_rule` VALUES (71, 0, 67, '获取管理员列表', '/admin/lists', 4);
INSERT INTO `cas_rule` VALUES (72, 0, 67, '编辑管理员状态', '/status/set', 5);

INSERT INTO `cas_rule` VALUES (73, 0, 11, '管理员日志', '', 2);
INSERT INTO `cas_rule` VALUES (74, 0, 73, '获取登录日志列表', '/log/login/lists', 1);
INSERT INTO `cas_rule` VALUES (75, 0, 73, '获取操作日志列表', '/log/operate/lists', 2);

INSERT INTO `cas_rule` VALUES (76, 0, 53, '角色管理', '', 3);
INSERT INTO `cas_rule` VALUES (77, 0, 76, '新增角色', '/role/add', 1);
INSERT INTO `cas_rule` VALUES (78, 0, 76, '编辑角色', '/role/update', 2);
INSERT INTO `cas_rule` VALUES (79, 0, 76, '删除角色', '/role/delete', 3);
INSERT INTO `cas_rule` VALUES (80, 0, 76, '获取角色列表', '/role/lists', 4);
INSERT INTO `cas_rule` VALUES (81, 0, 76, '复制角色', '/role/copy', 5);
INSERT INTO `cas_rule` VALUES (82, 0, 76, '编辑角色状态', '/role/status/set', 6);
INSERT INTO `cas_rule` VALUES (83, 0, 76, '获取角色菜单列表', '/role/rule/tree', 7);
INSERT INTO `cas_rule` VALUES (84, 0, 76, '更新角色菜单权限', '/role/rule/update', 8);

INSERT INTO `cas_rule` VALUES (85, 0, 2, '商家管理', '', 1);
INSERT INTO `cas_rule` VALUES (86, 0, 85, '新增/编辑商家', '/shop/save', 1);
INSERT INTO `cas_rule` VALUES (87, 0, 85, '删除商家', '/shop/delete', 1);
INSERT INTO `cas_rule` VALUES (88, 0, 85, '获取商家列表', '/shop/lists', 1);
INSERT INTO `cas_rule` VALUES (89, 0, 85, '禁用/启用商家', '/shop/status', 1);
INSERT INTO `cas_rule` VALUES (90, 0, 85, '查询商家详情', '/shop/selectById', 1);
INSERT INTO `cas_rule` VALUES (91, 0, 85, '行业类别管理', '/shop/cate/*', 1);
INSERT INTO `cas_rule` VALUES (92, 0, 85, '资质认证管理', '/shop/qualification/*', 1);
INSERT INTO `cas_rule` VALUES (93, 0, 85, '商家管理者管理', '/shop/manager/*', 1);

INSERT INTO `cas_rule` VALUES (94, 1, 8, '商户管理', '', 1);
INSERT INTO `cas_rule` VALUES (95, 0, 94, '新增商户', '/merchant/add,/merchant/image/upload', 1);
INSERT INTO `cas_rule` VALUES (96, 0, 94, '编辑商户', '/merchant/update,/merchant/image/upload', 1);
INSERT INTO `cas_rule` VALUES (97, 0, 94, '删除商户', '/merchant/delete', 1);
INSERT INTO `cas_rule` VALUES (98, 0, 94, '查询商户列表', '/merchant/get/list', 1);
INSERT INTO `cas_rule` VALUES (99, 0, 94, '查询商户详情', '/merchant/get', 1);
INSERT INTO `cas_rule` VALUES (100, 0, 94, '更改启用状态', '/merchant/status/set', 1);

INSERT INTO `cas_rule` VALUES (101, 1, 4, '商品订单', '', 1);
INSERT INTO `cas_rule` VALUES (102, 0, 101, '导入物流单号', '/product_order/un_dispatched/export,/product_order/express_no/import', 1);
INSERT INTO `cas_rule` VALUES (103, 0, 101, '获取列表', '/product_order/lists,/product_order/stat', 1);
INSERT INTO `cas_rule` VALUES (104, 0, 101, '导出列表数据', '/product_order/export', 1);
INSERT INTO `cas_rule` VALUES (105, 0, 101, '获取详情', '/product_order/info', 1);
INSERT INTO `cas_rule` VALUES (106, 0, 101, '退款', '/product_order/refund', 1);
INSERT INTO `cas_rule` VALUES (107, 1, 4, '退款审核', '', 2);
INSERT INTO `cas_rule` VALUES (108, 0, 107, '获取列表', '/product_order/refund/apply/lists', 1);
INSERT INTO `cas_rule` VALUES (109, 0, 107, '获取详情', '/product_order/refund/apply/info', 1);
INSERT INTO `cas_rule` VALUES (110, 0, 107, '审核', '/product_order/refund/audit', 1);

INSERT INTO `cas_rule` VALUES (111, 1, 85, '商品管理', '', 1);
INSERT INTO `cas_rule` VALUES (112, 0, 111, '新增/编辑商品', '/product/save', 1);
INSERT INTO `cas_rule` VALUES (113, 0, 111, '获取商品列表', '/product/lists', 1);
INSERT INTO `cas_rule` VALUES (114, 0, 111, '导出商品数据', '/product/export', 1);
INSERT INTO `cas_rule` VALUES (115, 0, 111, '获取商品信息', '/product/selectById', 1);
INSERT INTO `cas_rule` VALUES (116, 0, 111, '批量删除商品', '/product/delete', 1);
INSERT INTO `cas_rule` VALUES (117, 0, 111, '更改库存', '/product/updateStock', 1);
INSERT INTO `cas_rule` VALUES (118, 0, 111, '导出券码', '/product/ticket/export', 1);
INSERT INTO `cas_rule` VALUES (119, 0, 111, '导入券码', '/product/ticket/import', 1);

INSERT INTO `cas_rule` VALUES (120, 0, 85, '海报管理', '/shop/poster/*', 1);
INSERT INTO `cas_rule` VALUES (121, 0, 85, '收款配置', '/shop/receipt/status,/merchant/get/list', 1);
INSERT INTO `cas_rule` VALUES (122, 0, 85, '合同信息', '/shop/contract', 1);
INSERT INTO `cas_rule` VALUES (123, 0, 85, '注销商家', '/shop/off', 1);



