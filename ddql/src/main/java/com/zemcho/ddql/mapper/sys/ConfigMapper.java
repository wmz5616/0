package com.zemcho.ddql.mapper.sys;

import com.zemcho.ddql.entity.sys.Config;
import org.apache.ibatis.annotations.Param;

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
}
