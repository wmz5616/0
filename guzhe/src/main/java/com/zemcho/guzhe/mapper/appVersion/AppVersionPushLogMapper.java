package com.zemcho.guzhe.mapper.appVersion;

import com.zemcho.guzhe.entity.app.AppVersionPushLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author HXH
 */
@Mapper
public interface AppVersionPushLogMapper {
    int insert(@Param("data") AppVersionPushLog data);

    List<AppVersionPushLog> selectLog(@Param("searchStrField1") String searchStrField1);
}
