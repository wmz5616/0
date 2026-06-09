package com.zemcho.guzhe.service.data;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.data.param.MonitorParam;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author HXH
 */
public interface MonitorService {
    Result userStat();

    Result activeStat();

    Result visitTrendStat(MonitorParam param);

    Result getOrderData();

    Result getBusinessEquipment(MonitorParam param);

    void businessExport(MonitorParam param, HttpServletResponse response);
}
