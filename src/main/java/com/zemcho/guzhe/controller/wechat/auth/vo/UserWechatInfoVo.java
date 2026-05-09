package com.zemcho.guzhe.controller.wechat.auth.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.guzhe.entity.cas.CasUser;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserWechatInfoVo {
    //用户id
    private Integer id;

    //手机号码
    private String phone;

    //昵称/姓名
    private String nickname;

    //头像
    private String avatar;

    //性别 0:未知 1:男 2:女
    private Integer sex;

    //是否已实名认证：0否，1是
    private Integer hasCertification;

    //姓名
    private String name;

    //管理员id
    private Integer adminId;

    //是否有券码核销权限
    private Boolean canCheckTicket = false;

    //金币数量
    private Integer goldCoin;

    //用户总健康币数量
    private Integer healthCoin;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    public static UserWechatInfoVo getInstance(CasUser user) {
        UserWechatInfoVo info = new UserWechatInfoVo();
        info.setId(user.getId());
        info.setPhone(user.getPhone());
        info.setNickname(user.getNickname());
        info.setAvatar(user.getAvatar());
        info.setSex(user.getSex());
        info.setHasCertification(user.getHasCertification());
        info.setName(user.getName());
        info.setAdminId(user.getAdminId());
//        info.setGoldCoin(user.getGoldCoin());
//        info.setHealthCoin(user.getHealthCoin());
        info.setCreateTime(user.getCreateTime());

        return info;
    }
}
