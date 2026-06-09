package com.zemcho.guzhe.controller.data.vo;

import lombok.Data;

@Data
public class VisitTrendStatVo {
    // 数据分组--X轴
    private String dataGroup;

    //打开次数
    private Long totalSessionCnt = 0L;

    //访问次数
    private Long totalVisitPv = 0L;

    //访问人数
    private Long totalVisitUv = 0L;

    //新用户数
    private Long totalVisitUvNew = 0L;
}