package com.zemcho.ddql.controller.statistic.vo;

import lombok.Data;

/**
 * @title: VisitTrendStatVo
 * @Description:
 * @Date: 2025/11/5 11:46
 */
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
