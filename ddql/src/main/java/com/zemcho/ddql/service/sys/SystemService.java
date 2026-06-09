package com.zemcho.ddql.service.sys;


import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.controller.sys.param.ConfigParam;

/**
 * @author Ryan
 * @title: SystemService
 * @projectName master
 * @description: ZEMCHO
 * @date 2021/3/26 0026 14:12
 */
public interface SystemService {
    /**
     * 获取系统基础配置信息
     *
     * @return
     */
    Result getBasicConfig();

    /**
     * 修改基础配置信息
     *
     * @param param
     * @return
     */
    Result updateBasicConfig(ConfigParam param);
}
