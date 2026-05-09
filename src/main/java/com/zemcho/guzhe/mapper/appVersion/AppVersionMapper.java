package com.zemcho.guzhe.mapper.appVersion;

import com.zemcho.guzhe.controller.appVersion.param.AppVersionParam;
import com.zemcho.guzhe.entity.app.AppVersion;
import com.zemcho.guzhe.entity.app.AppVersionPushLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AppVersionMapper {

    @Select("select count(*) from app_version where id = #{id} and delete_time is null")
    Boolean ifExistById(@Param("id") Integer id);

    @Select("select count(*) from app_version where serial_number = #{serialNumber} and id != #{id} and delete_time is null")
    Boolean ifExistBySerialNumber(@Param("serialNumber") String serialNumber, @Param("id") Integer id);

    int insert(@Param("data") AppVersionParam data);

    int update(@Param("data") AppVersionParam data);

    List<AppVersion> select();

    int delete(@Param("id") Integer id);

    @Select("select * from app_version where id = #{id} and delete_time is null")
    AppVersion selectById(@Param("id") Integer id);

    AppVersionPushLog selectPublishLog(@Param("id")Integer id);

    /**
     * 获取最新版本信息
     *
     * @param nowRelease
     * @return
     */
    AppVersion selectLatestVersion(@Param("nowRelease") Integer nowRelease);
}
