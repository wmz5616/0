package com.zemcho.guzhe.mapper.log;

import com.zemcho.guzhe.controller.log.param.LogParam;
import com.zemcho.guzhe.entity.log.LoginLog;
import com.zemcho.guzhe.entity.log.OperateLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Ryan
 * @title: LogMapper
 * @projectName zemcho_table
 * @description: ZEMCHO
 * @date 2021/9/13 0013 15:26
 */
public interface LogMapper {
    /**
     * 新增登录日志记录
     *
     * @param log
     * @return
     */
    Integer insertLoginLog(@Param("log") LoginLog log);

    /**
     * 新增操作日志记录
     *
     * @param log
     * @return
     */
    Integer insertOperateLog(@Param("log") OperateLog log);

    /**
     * 查询登录日志记录列表
     *
     * @param param
     * @return
     */
    List<LoginLog> selectLoginLog(@Param("param") LogParam param);

    /**
     * 查询操作日志记录列表
     *
     * @param param
     * @return
     */
    List<OperateLog> selectOperateLog(@Param("param") LogParam param);
}
