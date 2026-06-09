package com.zemcho.ddql.service.log;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.dto.AuthAttrData;
import com.zemcho.ddql.controller.log.param.LogParam;

public interface LogService {
    /**
     * 获取登录日志列表
     *
     * @param param
     * @return
     */
    Result getLoginLogList(LogParam param);

    /**
     * 获取操作日志列表
     *
     * @param param
     * @param authAttrData
     * @return
     */
    Result getOperateLogList(LogParam param, AuthAttrData authAttrData);
}
