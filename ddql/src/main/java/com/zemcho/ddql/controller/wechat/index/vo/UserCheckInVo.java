package com.zemcho.ddql.controller.wechat.index.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.ddql.entity.cas.CasUserCheckInTeamRecord;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @title: UserCheckInVo
 * @Description:
 * @Date: 2025/10/9 19:38
 */
@Data
public class UserCheckInVo {
    // 主键ID
    private Integer id;

    // 用户id
    private Integer userId;

    // 用户昵称
    private String nickname;

    // 用户手机号
    private String phone;

    // 用户头像
    private String avatar;

    // 场地id
    private Integer placeId;

    // 场地名称
    private String placeName;

    // 场地地址
    private String placeAddress;

    // 场地经纬度
    private String location;

    // 打卡方式 0扫码打卡 1距离打卡
    private Integer checkInMethod;

    //设备id，扫码打卡时才有
    private Integer equipmentId;

    // 打卡日期
    private LocalDate date;

    // 打卡开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    // 打卡结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    // 打卡总时长（秒）
    private Integer checkInTime;

    // 状态：1打卡中、2打卡成功、3打卡失效
    private Integer status;

    //本次打卡获得的总健康币数量
    private Integer healthCoin;

    //本次打卡获得的总金币数量
    private Integer goldCoin;

    // 本次打卡的团体数据
    private List<CasUserCheckInTeamRecord> teamData;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
