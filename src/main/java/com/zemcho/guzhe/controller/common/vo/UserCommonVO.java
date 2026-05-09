package com.zemcho.guzhe.controller.common.vo;

import lombok.Data;

/**
 * @title: UserCommonVO
 * @Description:
 * @Date: 2025/5/12 18:34
 */
@Data
public class UserCommonVO {
    //用户id
    private Integer id;

    //昵称
    private String nickname;

    //手机号码
    private String phone;

    //头像
    private String avatar;

    //性别 0:未知 1:男 2:女
    private Integer sex;

    //是否已实名认证：0否，1是
    private Integer hasCertification;

    //身份证号码
    private String cardNum;

    //姓名
    private String name;

    //用户锁 0正常 1暂时锁定 2永久锁定
    private Integer lock;

    //管理员id
    private Integer adminId;
}
