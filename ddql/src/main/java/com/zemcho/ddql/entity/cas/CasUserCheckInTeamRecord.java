package com.zemcho.ddql.entity.cas;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CasUserCheckInTeamRecord {
    // 主键ID
    private Integer id;

    // 打卡记录id
    private Integer recordId;

    // 团队id
    private Integer teamId;

    // 团队名字
    private String teamName;

    // 团队类型 0企事单位 1政府部分 2家庭 3朋友
    private Integer teamType;

    // 用户id
    private Integer userId;

    // 场地id
    private Integer placeId;

    // 打卡方式 0扫码打卡 1距离打卡
    private Integer checkInMethod;

    // 打卡日期
    private LocalDate date;

    //获取类型：1时长、2步数、3时长+步数
    private Integer obtainType;

    //本次打卡获得的团队健康币数量
    private Integer healthCoin;

    //排名
    private Integer rank;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
