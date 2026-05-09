INSERT INTO `sys_config` (`key`, `value`, `remark`, `type`, `create_time`, `update_time`)
VALUES
    ('show_merchant_notice', '0', '是否展示入驻前须知：0-否，1-是', 2, NOW(), NOW()),
    ('merchant_notice', '', '入驻前须知内容（富文本，支持上传图片）', 2, NOW(), NOW());
