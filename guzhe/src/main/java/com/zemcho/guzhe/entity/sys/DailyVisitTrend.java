package com.zemcho.guzhe.entity.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @title: DailyVisitTrend
 * @Description:
 * @Date: 2025/10/27 17:33
 */
@Data
public class DailyVisitTrend {
    private Integer id;

    //年份
    private Integer year;

    //月份
    private String month;

    //日期
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    //打开次数
    private Long sessionCnt;

    //访问次数
    private Long visitPv;

    //访问人数
    private Long visitUv;

    //新用户数
    private Long visitUvNew;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
