package com.zemcho.guzhe.mapper.sys;

import com.zemcho.guzhe.entity.sys.Config;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ConfigMapper {
    /**
     * 根据配置类型获取配置列表
     *
     * @param type
     * @return
     */
    List<Config> selectConfigListByType(@Param("type") Integer type);

    /**
     * 根据key更新value
     *
     * @param key
     * @param value
     * @return
     */
    Integer updateConfigValueByKey(@Param("key") String key, @Param("value") String value);

    @Select("SELECT id, `key`, value, remark, type, create_time, update_time FROM sys_config WHERE `key` = #{key} LIMIT 1")
    Config selectConfigByKey(@Param("key")String key);
}
