package com.zemcho.ddql.service.statistic;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.controller.statistic.param.MonitorParam;

public interface MonitorService {
    /**
     * 系统数据统计
     *
     * @param param
     * @return
     */
    Result systemStat(MonitorParam param);

    /**
     * 平台用户统计
     *
     * @return
     */
    Result userStat();

    /**
     * 用户活跃度统计
     *
     * @return
     */
    Result activeStat();

    /**
     * 平台流量情况
     *
     * @param param
     * @return
     */
    Result visitTrendStat(MonitorParam param);

    /**
     * 场地打卡量排行榜
     *
     * @param param
     * @return
     */
    Result placeCheckInRankLists(MonitorParam param);
}
