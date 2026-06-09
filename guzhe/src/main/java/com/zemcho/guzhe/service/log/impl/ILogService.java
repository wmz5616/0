package com.zemcho.guzhe.service.log.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthAttrData;
import com.zemcho.guzhe.controller.log.param.LogParam;
import com.zemcho.guzhe.entity.log.LoginLog;
import com.zemcho.guzhe.entity.log.OperateLog;
import com.zemcho.guzhe.mapper.log.LogMapper;
import com.zemcho.guzhe.service.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @title: ILogService
 * @Description:
 * @Date: 2024/1/24 10:10
 */
@Service
public class ILogService implements LogService {
    @Autowired
    LogMapper logMapper;

    /**
     * 获取登录日志列表
     *
     * @param param
     * @return
     */
    @Override
    public Result getLoginLogList(LogParam param) {
        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<LoginLog> logs = logMapper.selectLoginLog(param);
        PageInfo<LoginLog> pageInfo = new PageInfo<>(logs);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 获取操作日志列表
     *
     * @param param
     * @return
     */
    @Override
    public Result getOperateLogList(LogParam param, AuthAttrData authAttrData) {
        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<OperateLog> logs = logMapper.selectOperateLog(param);
        PageInfo<OperateLog> pageInfo = new PageInfo<>(logs);

        return Result.success("获取成功", pageInfo);
    }
}
