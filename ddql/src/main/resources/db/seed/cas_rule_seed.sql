-- ----------------------------
-- Records of cas_rule
-- ----------------------------
INSERT INTO `cas_rule` VALUES (1, 1, 0, '数据总览', '', 1);
INSERT INTO `cas_rule` VALUES (2, 1, 0, '团体管理', '', 2);
INSERT INTO `cas_rule` VALUES (3, 1, 0, '打卡设置', '', 3);
INSERT INTO `cas_rule` VALUES (4, 1, 0, '充值设置', '', 4);
INSERT INTO `cas_rule` VALUES (5, 1, 0, '设备管理', '', 5);
INSERT INTO `cas_rule` VALUES (6, 1, 0, '兑换商品管理', '', 6);
INSERT INTO `cas_rule` VALUES (7, 1, 0, '商圈信息管理', '', 7);
INSERT INTO `cas_rule` VALUES (8, 1, 0, '通知公告', '', 8);
INSERT INTO `cas_rule` VALUES (9, 1, 0, '用户管理', '', 9);
INSERT INTO `cas_rule` VALUES (10, 1, 0, '权限管理', '', 10);
INSERT INTO `cas_rule` VALUES (11, 1, 0, '系统设置', '', 11);
INSERT INTO `cas_rule` VALUES (12, 1, 0, 'APP版本控制', '', 12);

INSERT INTO `cas_rule` VALUES (13, 0, 2, '删除团体', '/team/delete', 1);
INSERT INTO `cas_rule` VALUES (14, 0, 2, '更新团队信息', '/team/update', 2);
INSERT INTO `cas_rule` VALUES (15, 0, 2, '查询团体列表', '/team/select', 3);
INSERT INTO `cas_rule` VALUES (16, 0, 2, '更新团体的用户信息', '/team/updateTeamUser', 4);
INSERT INTO `cas_rule` VALUES (17, 0, 2, '查询团体下用户信息', '/team/selectTeamUser', 5);
INSERT INTO `cas_rule` VALUES (18, 0, 2, '查询团体下资质审核记录', '/team/selectVerificationRecord', 6);
INSERT INTO `cas_rule` VALUES (19, 0, 2, '人工审核团体资质审核记录', '/team/auditVerificationRecord', 7);
INSERT INTO `cas_rule` VALUES (20, 0, 2, '导出团队列表', '/team/export', 8);

INSERT INTO `cas_rule` VALUES (21, 0, 9, '更新用户信息', '/user/update', 1);
INSERT INTO `cas_rule` VALUES (22, 0, 9, '查询用户列表', '/user/lists', 2);
INSERT INTO `cas_rule` VALUES (23, 0, 9, '查询用户信息', '/user/detail', 3);
INSERT INTO `cas_rule` VALUES (24, 0, 9, '导出用户信息', '/user/export', 4);

INSERT INTO `cas_rule` VALUES (25, 1, 3, '打卡设置', '', 1);
INSERT INTO `cas_rule` VALUES (26, 1, 3, '打卡类型', '', 2);
INSERT INTO `cas_rule` VALUES (27, 1, 3, '打卡场所', '', 3);
INSERT INTO `cas_rule` VALUES (28, 0, 25, '获取打卡设置', '/checkInSettings/get',1);
INSERT INTO `cas_rule` VALUES (29, 0, 25, '更新打卡设置', '/checkInSettings/update',2);
INSERT INTO `cas_rule` VALUES (30, 0, 26, '新增打卡类型', '/checkInType/add',1);
INSERT INTO `cas_rule` VALUES (31, 0, 26, '修改打卡类型', '/checkInType/update',2);
INSERT INTO `cas_rule` VALUES (32, 0, 26, '删除打卡类型', '/checkInType/delete',3);
INSERT INTO `cas_rule` VALUES (33, 0, 26, '获取打卡类型列表', '/checkInType/getCheckInTypeList',4);
INSERT INTO `cas_rule` VALUES (34, 0, 27, '新增打卡场所', '/checkInPlace/add',1);
INSERT INTO `cas_rule` VALUES (35, 0, 27, '修改打卡场所', '/checkInPlace/update',2);
INSERT INTO `cas_rule` VALUES (36, 0, 27, '删除打卡场所', '/checkInPlace/delete',3);
INSERT INTO `cas_rule` VALUES (37, 0, 27, '获取打卡场所列表', '/checkInPlace/select',4);
INSERT INTO `cas_rule` VALUES (38, 0, 27, '获取打卡场所管理员信息', '/checkInPlace/selectUserByPlaceId',5);

INSERT INTO `cas_rule` VALUES (39, 0, 5, '新增设备', '/equipment/add', 1);
INSERT INTO `cas_rule` VALUES (40, 0, 5, '修改设备', '/equipment/update', 2);
INSERT INTO `cas_rule` VALUES (41, 0, 5, '删除设备', '/equipment/delete', 3);
INSERT INTO `cas_rule` VALUES (42, 0, 5, '获取设备列表', '/equipment/select', 4);
INSERT INTO `cas_rule` VALUES (43, 0, 5, '保存设备海报', '/equipmentPoster/save', 5);
INSERT INTO `cas_rule` VALUES (44, 0, 5, '获取设备海报', '/equipmentPoster/select', 6);

INSERT INTO `cas_rule` VALUES (45, 0, 8, '新增通知公告', '/notice/add', 1);
INSERT INTO `cas_rule` VALUES (46, 0, 8, '修改通知公告', '/notice/update', 2);
INSERT INTO `cas_rule` VALUES (47, 0, 8, '删除通知公告', '/notice/delete', 3);
INSERT INTO `cas_rule` VALUES (48, 0, 8, '获取通知公告列表', '/notice/getNoticeList', 4);
INSERT INTO `cas_rule` VALUES (49, 0, 8, '获取通知公告详情', '/notice/getInfo', 5);

INSERT INTO `cas_rule` VALUES (50, 1, 11, '关于我们', '', 1);
INSERT INTO `cas_rule` VALUES (51, 0, 50, '获取系统配置', '/system/basic/config', 1);
INSERT INTO `cas_rule` VALUES (52, 0, 50, '更新系统配置', '/system/config/update', 2);
INSERT INTO `cas_rule` VALUES (53, 1, 11, '文章管理', '', 2);
INSERT INTO `cas_rule` VALUES (54, 0, 53, '新增文章', '/article/add', 1);
INSERT INTO `cas_rule` VALUES (55, 0, 53, '修改文章', '/article/update', 2);
INSERT INTO `cas_rule` VALUES (56, 0, 53, '删除文章', '/article/delete', 3);
INSERT INTO `cas_rule` VALUES (57, 0, 53, '获取文章列表', '/article/select', 4);

INSERT INTO `cas_rule` VALUES (58, 1, 11, '用户端设置', '', 1);
INSERT INTO `cas_rule` VALUES (59, 0, 58, '新增首页轮播图', '/homePage/banner/insert', 1);
INSERT INTO `cas_rule` VALUES (60, 0, 58, '修改首页轮播图', '/homePage/banner/update', 2);
INSERT INTO `cas_rule` VALUES (61, 0, 58, '删除首页轮播图', '/homePage/banner/delete', 3);
INSERT INTO `cas_rule` VALUES (62, 0, 58, '获取首页轮播图列表', '/homePage/banner/select', 4);
INSERT INTO `cas_rule` VALUES (63, 0, 58, '获取展示中的首页轮播图列表', '/homePage/banner/showList,/homePage/banner/allList', 4);

INSERT INTO `cas_rule` VALUES (64, 0, 12, '新增版本', '/appVersion/add', 1);
INSERT INTO `cas_rule` VALUES (65, 0, 12, '修改版本', '/appVersion/update', 2);
INSERT INTO `cas_rule` VALUES (66, 0, 12, '删除版本', '/appVersion/delete', 3);
INSERT INTO `cas_rule` VALUES (67, 0, 12, '获取版本列表', '/appVersion/select', 4);

INSERT INTO `cas_rule` VALUES (68, 1, 6, '商品管理', '', 1);
INSERT INTO `cas_rule` VALUES (69, 0, 68, '新增/编辑商品', '/product/save', 1);
INSERT INTO `cas_rule` VALUES (70, 0, 68, '获取商品列表', '/product/lists', 2);
INSERT INTO `cas_rule` VALUES (71, 0, 68, '根据id批量删除商品', '/product/delete', 3);
INSERT INTO `cas_rule` VALUES (72, 0, 68, '更改库存', '/product/updateStock', 4);
INSERT INTO `cas_rule` VALUES (73, 0, 68, '导出券码', '/product/ticket/export', 5);
INSERT INTO `cas_rule` VALUES (74, 0, 68, '导入券码', '/product/ticket/import', 6);
INSERT INTO `cas_rule` VALUES (75, 0, 68, '新增商品分类', '/product/category/add', 7);
INSERT INTO `cas_rule` VALUES (76, 0, 68, '获取商品分类列表', '/product/category/lists', 8);
INSERT INTO `cas_rule` VALUES (77, 0, 68, '根据id删除商品分类', '/product/category/delete', 9);

INSERT INTO `cas_rule` VALUES (78, 1, 7, '商圈信息管理', '', 1);
INSERT INTO `cas_rule` VALUES (79, 0, 78, '新增商圈', '/business/circle/save', 1);
INSERT INTO `cas_rule` VALUES (80, 0, 78, '修改商圈', '/business/circle/update', 2);
INSERT INTO `cas_rule` VALUES (81, 0, 78, '条件查询商圈列表', '/business/circle/lists', 3);
INSERT INTO `cas_rule` VALUES (82, 0, 78, '禁用/启用商圈', '/business/circle/status', 4);
INSERT INTO `cas_rule` VALUES (83, 0, 78, '删除商圈', '/business/circle/delete', 5);

INSERT INTO `cas_rule` VALUES (84, 0, 7, '店铺信息管理', '', 2);
INSERT INTO `cas_rule` VALUES (85, 0, 84, '新增/编辑店铺', '/business/shop/save', 1);
INSERT INTO `cas_rule` VALUES (86, 0, 84, '删除店铺', '/business/shop/delete', 2);
INSERT INTO `cas_rule` VALUES (87, 0, 84, '获取店铺列表', '/business/shop/lists', 3);
INSERT INTO `cas_rule` VALUES (88, 0, 84, '禁用/启用店铺', '/business/shop/status', 4);

INSERT INTO `cas_rule` VALUES (89, 1, 4, '充值配置', '', 1);
INSERT INTO `cas_rule` VALUES (90, 0, 89, '新增充值配置', '/recharge/config/add', 1);
INSERT INTO `cas_rule` VALUES (91, 0, 89, '编辑充值配置', '/recharge/config/update', 2);
INSERT INTO `cas_rule` VALUES (92, 0, 89, '查询充值配置信息', '/recharge/config/getInfo', 3);

INSERT INTO `cas_rule` VALUES (93, 0, 4, '充值活动', '', 2);
INSERT INTO `cas_rule` VALUES (94, 0, 93, '新增/编辑充值活动', '/recharge/activity/save', 1);
INSERT INTO `cas_rule` VALUES (95, 0, 93, '获取充值活动列表', '/recharge/activity/lists', 2);
INSERT INTO `cas_rule` VALUES (96, 0, 93, '根据id删除充值活动', '/recharge/activity/delete', 3);
INSERT INTO `cas_rule` VALUES (97, 0, 93, '根据id进行充值活动排序', '/recharge/activity/sort', 4);

INSERT INTO `cas_rule` VALUES (98, 0, 68, '根据id进行商品分类排序', '/product/category/sort', 10);
INSERT INTO `cas_rule` VALUES (99, 0, 68, '根据id获取商品信息', '', 11);

INSERT INTO `cas_rule` VALUES (100, 0, 84, '根据id查询店铺详情', '/business/shop/selectById', 5);
INSERT INTO `cas_rule` VALUES (101, 0, 78, '根据id查询商圈信息', '/business/circle/selectById', 6);

INSERT INTO `cas_rule` VALUES (102, 1, 6, '兑换订单', '', 2);
INSERT INTO `cas_rule` VALUES (103, 0, 102, '获取列表', '/order/exchange/lists,/order/exchange/stat', 1);
INSERT INTO `cas_rule` VALUES (104, 0, 102, '导出列表数据', '/order/exchange/export', 1);
INSERT INTO `cas_rule` VALUES (105, 0, 102, '获取详情', '/order/exchange/info', 1);
INSERT INTO `cas_rule` VALUES (106, 0, 102, '退货', '/order/exchange/refund', 1);
INSERT INTO `cas_rule` VALUES (107, 1, 6, '退货审核', '', 3);
INSERT INTO `cas_rule` VALUES (108, 0, 107, '获取列表', '/order/exchange/refund/apply/lists', 1);
INSERT INTO `cas_rule` VALUES (109, 0, 107, '获取详情', '/order/exchange/refund/apply/info', 1);
INSERT INTO `cas_rule` VALUES (110, 0, 107, '审核', '/order/exchange/refund/audit', 1);

INSERT INTO `cas_rule` VALUES (111, 0, 2, '编辑团队状态', '/team/status/set', 1);
INSERT INTO `cas_rule` VALUES (112, 0, 2, '新增团体资质认证', '/team/addVerificationRecord', 1);
INSERT INTO `cas_rule` VALUES (113, 0, 2, '编辑团体资质认证', '/team/updateVerificationRecord', 1);

INSERT INTO `cas_rule` VALUES (114, 0, 26, '修改打卡类型顺序', '/checkInType/sort/set', 1);
INSERT INTO `cas_rule` VALUES (115, 0, 27, '编辑打卡场所状态', '/checkInPlace/status/set', 1);
INSERT INTO `cas_rule` VALUES (116, 0, 5, '获取设备日志列表', '/equipment/log/lists', 1);
INSERT INTO `cas_rule` VALUES (117, 0, 5, '编辑设备状态', '/equipment/status/set', 1);
INSERT INTO `cas_rule` VALUES (118, 0, 5, '修改设备海报顺序', '/equipmentPoster/sort/set', 1);
INSERT INTO `cas_rule` VALUES (119, 0, 68, '导出商品数据', '/product/export', 1);
INSERT INTO `cas_rule` VALUES (120, 0, 9, '获取提现列表', '/order/withdrawal/lists', 1);
INSERT INTO `cas_rule` VALUES (121, 0, 9, '导出提现数据', '/order/withdrawal/export', 1);
INSERT INTO `cas_rule` VALUES (122, 0, 9, '获取提现详情', '/order/withdrawal/info', 1);
INSERT INTO `cas_rule` VALUES (123, 0, 9, '获取兑换订单列表', '/order/exchange/lists,/order/exchange/stat', 1);
INSERT INTO `cas_rule` VALUES (124, 0, 9, '导出兑换订单数据', '/order/exchange/export', 1);
INSERT INTO `cas_rule` VALUES (125, 0, 9, '获取兑换订单详情', '/order/exchange/info', 1);

INSERT INTO `cas_rule` VALUES (126, 1, 10, '管理员管理', '', 1);
INSERT INTO `cas_rule` VALUES (127, 0, 126, '新增管理员', '/admin/add', 1);
INSERT INTO `cas_rule` VALUES (128, 0, 126, '编辑管理员', '/admin/update', 2);
INSERT INTO `cas_rule` VALUES (129, 0, 126, '获取管理员列表', '/admin/lists', 3);
INSERT INTO `cas_rule` VALUES (130, 0, 126, '编辑管理员状态', '/admin/status/set', 4);
INSERT INTO `cas_rule` VALUES (131, 0, 126, '删除管理员', '/admin/delete', 5);

INSERT INTO `cas_rule` VALUES (132, 1, 10, '管理员日志', '', 2);
INSERT INTO `cas_rule` VALUES (133, 0, 132, '获取操作日志列表', '/log/operate/lists', 1);
INSERT INTO `cas_rule` VALUES (134, 0, 132, '获取登录日志列表', '/log/login/lists', 1);

INSERT INTO `cas_rule` VALUES (135, 1, 10, '角色管理', '', 3);
INSERT INTO `cas_rule` VALUES (136, 0, 135, '新增角色', '/role/add', 1);
INSERT INTO `cas_rule` VALUES (137, 0, 135, '编辑角色', '/role/update', 2);
INSERT INTO `cas_rule` VALUES (138, 0, 135, '复制角色', '/role/copy', 3);
INSERT INTO `cas_rule` VALUES (139, 0, 135, '获取角色列表', '/role/lists', 4);
INSERT INTO `cas_rule` VALUES (140, 0, 135, '删除角色', '/role/delete', 5);
INSERT INTO `cas_rule` VALUES (141, 0, 135, '编辑角色状态', '/role/status/set', 6);
INSERT INTO `cas_rule` VALUES (142, 0, 135, '获取角色菜单列表', '/role/rule/tree', 7);
INSERT INTO `cas_rule` VALUES (143, 0, 135, '更新角色菜单权限', '/role/rule/update', 8);

INSERT INTO `cas_rule` VALUES (144, 0, 2, '编辑团体用户状态', '/team/user/status/set', 1);
INSERT INTO `cas_rule` VALUES (145, 0, 68, '获取商品详情', '/product/selectById', 1);
INSERT INTO `cas_rule` VALUES (146, 0, 2, '统计团队充值订单数据', '/team/recharge/order/count', 1);
INSERT INTO `cas_rule` VALUES (147, 0, 2, '新增团队', '/team/add', 1);

INSERT INTO `cas_rule` VALUES (148, 0, 5, '删除设备海报', '/equipmentPoster/delete', 1);

INSERT INTO `cas_rule` VALUES (149, 1, 2, '充值记录', '', 4);
INSERT INTO `cas_rule` VALUES (150, 0, 149, '获取列表', '/order/recharge/lists,/order/recharge/stat', 1);
INSERT INTO `cas_rule` VALUES (151, 0, 149, '导出', '/order/recharge/export', 1);
INSERT INTO `cas_rule` VALUES (152, 0, 149, '获取详情', '/order/recharge/info', 1);
INSERT INTO `cas_rule` VALUES (153, 0, 149, '退款', '/order/recharge/refund', 1);

INSERT INTO `cas_rule` VALUES (154, 1, 4, '充值记录', '', 3);
INSERT INTO `cas_rule` VALUES (155, 0, 154, '获取列表', '/order/recharge/lists,/order/recharge/stat', 1);
INSERT INTO `cas_rule` VALUES (156, 0, 154, '导出', '/order/recharge/export', 1);
INSERT INTO `cas_rule` VALUES (157, 0, 154, '获取详情', '/order/recharge/info', 1);
INSERT INTO `cas_rule` VALUES (158, 0, 154, '退款', '/order/recharge/refund', 1);

INSERT INTO `cas_rule` VALUES (159, 0, 58, '编辑首页轮播图状态', '/homePage/banner/status/set', 1);
INSERT INTO `cas_rule` VALUES (160, 0, 8, '设置通知公告置顶状态', '/notice/top/set', 1);
INSERT INTO `cas_rule` VALUES (161, 0, 2, '删除团体用户', '/team/user/delete', 1);
INSERT INTO `cas_rule` VALUES (162, 0, 53, '更改文章展示顺序', '/article/sort/set', 1);

INSERT INTO `cas_rule` VALUES (163, 0, 1, '平台用户统计', '/monitor/user/stat', 1);
INSERT INTO `cas_rule` VALUES (164, 0, 1, '用户活跃度统计', '/monitor/active/stat', 1);
INSERT INTO `cas_rule` VALUES (165, 0, 1, '平台流量情况', '/monitor/visit/trend/stat', 1);

INSERT INTO `cas_rule` VALUES (166, 0, 102, '未发货数据导出', '/order/exchange/un_dispatched/export', 1);
INSERT INTO `cas_rule` VALUES (167, 0, 102, '导入物流单号', '/order/exchange/express_no/import', 1);

INSERT INTO `cas_rule` VALUES (168, 0, 1, '系统数据统计', '/monitor/system/stat', 1);
INSERT INTO `cas_rule` VALUES (169, 0, 1, '场地打卡量排行榜', '/monitor/place/check_in/rank/lists', 1);

INSERT INTO `cas_rule` VALUES (170, 0, 58, '修改首页轮播图顺序', '/homePage/banner/sort/set', 1);

INSERT INTO `cas_rule` VALUES (171, 1, 0, '商户管理', '', 9);
INSERT INTO `cas_rule` VALUES (172, 0, 171, '新增商户', '/merchant/add,/merchant/image/upload', 1);
INSERT INTO `cas_rule` VALUES (173, 0, 171, '编辑商户', '/merchant/update,/merchant/image/upload', 1);
INSERT INTO `cas_rule` VALUES (174, 0, 171, '删除商户', '/merchant/delete', 1);
INSERT INTO `cas_rule` VALUES (175, 0, 171, '查询商户列表', '/merchant/select', 1);
INSERT INTO `cas_rule` VALUES (176, 0, 171, '查询商户详情', '/merchant/selectById', 1);
INSERT INTO `cas_rule` VALUES (177, 0, 171, '更改启用状态', '/merchant/status/set', 1);

INSERT INTO `cas_rule` VALUES (178, 1, 0, '商家管理', '', 13);
INSERT INTO `cas_rule` VALUES (179, 0, 178, '查询审核信息列表', '/audit/list', 1);
INSERT INTO `cas_rule` VALUES (180, 0, 178, '处理审核信息', '/audit/handle', 1);
INSERT INTO `cas_rule` VALUES (181, 0, 178, '查询商家申请入驻审核的商家详情', '/audit/get', 1);

INSERT INTO `cas_rule` VALUES (182, 1, 178, '用币规则', '', 1);
INSERT INTO `cas_rule` VALUES (183, 0, 182, '新增用币规则', '/business/shop/coin/add', 1);
INSERT INTO `cas_rule` VALUES (184, 0, 182, '编辑用币规则', '/business/shop/coin/update', 1);
INSERT INTO `cas_rule` VALUES (185, 0, 182, '根据商家Id查询', '/business/shop/coin/get', 1);

INSERT INTO `cas_rule` VALUES (186, 1, 178, '资质认证', '', 1);
INSERT INTO `cas_rule` VALUES (187, 0, 186, '提交资质认证', '/business/shop/qualification/add', 1);
INSERT INTO `cas_rule` VALUES (188, 0, 186, '修改资质认证', '/business/shop/qualification/update', 1);
INSERT INTO `cas_rule` VALUES (189, 0, 186, '根据商家ID查询资质认证信息', '/business/shop/qualification/get', 1);
INSERT INTO `cas_rule` VALUES (190, 0, 186, '审核资质认证信息', '/business/shop/qualification/audit', 1);

INSERT INTO `cas_rule` VALUES (191, 1, 178, '收款配置', '', 1);
INSERT INTO `cas_rule` VALUES (192, 0, 191, '修改商家收款配置', '/business/shop/receipt/status', 1);

INSERT INTO `cas_rule` VALUES (193, 1, 178, '商家合同', '', 1);
INSERT INTO `cas_rule` VALUES (194, 0, 193, '修改商家合同照片', '/business/shop/contract', 1);

INSERT INTO `cas_rule` VALUES (195, 1, 178, '商家管理人员', '', 1);
INSERT INTO `cas_rule` VALUES (196, 0, 195, '新增商家管理人员', '/business/shop/manager/add', 1);
INSERT INTO `cas_rule` VALUES (197, 0, 195, '修改商家管理人员', '/business/shop/manager/update', 1);
INSERT INTO `cas_rule` VALUES (198, 0, 195, '删除商家管理人员', '/business/shop/manager/del', 1);
INSERT INTO `cas_rule` VALUES (199, 0, 195, '根据商家ID查询管理人员', '/business/shop/manager/get', 1);

INSERT INTO `cas_rule` VALUES (200, 1, 178, '行业类别', '', 1);
INSERT INTO `cas_rule` VALUES (201, 0, 200, '新增行业类别', '/business/shop/cate/add', 1);
INSERT INTO `cas_rule` VALUES (202, 0, 200, '修改行业类别', '/business/shop/cate/update', 1);
INSERT INTO `cas_rule` VALUES (203, 0, 200, '删除行业类别', '/business/shop/cate/del', 1);
INSERT INTO `cas_rule` VALUES (204, 0, 200, '查询行业类别列表', '/business/shop/cate/get', 1);
INSERT INTO `cas_rule` VALUES (205, 0, 200, '调整行业类别排序', '/business/shop/cate/update/sort', 1);

INSERT INTO `cas_rule` VALUES (206, 0, 178, '生成收款二维码', '/business/shop/generateQrCode', 1);
INSERT INTO `cas_rule` VALUES (207, 0, 178, '下载收款二维码', '/business/shop/downloadQrCode', 1);

INSERT INTO `cas_rule` VALUES (208, 0, 178, '禁用/启用商家消费置顶', '/business/shop/topConsumption/status', 1);