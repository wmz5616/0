package com.zemcho.ddql.entity.cas;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CasUserSportRecord {
    // 主键ID
    private Integer id;

    // 用户id
    private Integer userId;

    // 日期
    private LocalDate date;

    // 当天总步数
    private Integer stepNum;

    // 当天打卡总时长（秒）
    private Integer checkInTime;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
