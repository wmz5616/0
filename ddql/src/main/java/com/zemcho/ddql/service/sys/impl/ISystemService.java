package com.zemcho.ddql.service.sys.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.controller.sys.param.ConfigParam;
import com.zemcho.ddql.entity.sys.Config;
import com.zemcho.ddql.mapper.sys.ConfigMapper;
import com.zemcho.ddql.service.sys.SystemService;
import com.zemcho.ddql.util.Constant;
import com.zemcho.ddql.util.StringUtil;
import com.zemcho.ddql.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Ryan
 * @title: ISystemService
 * @projectName master
 * @description: ZEMCHO
 * @date 2021/3/26 0026 14:12
 */
@Service
@Transactional
public class ISystemService implements SystemService {
    @Autowired
    ConfigMapper configMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取系统基础配置信息
     *
     * @return
     */
    @Override
    public Result getBasicConfig() {
        Integer configType = 1;
        String cacheKey = getConfigCacheKey(configType);

        Object configObject = redisUtil.get(cacheKey);
        List<Config> configList = null;
        if (configObject != null) {
            configList = JSON.parseArray((String) configObject, Config.class);
        }

        if (configList == null || configList.isEmpty()) {
            configList = configMapper.selectConfigListByType(configType);
            if (configList != null && !configList.isEmpty()) {
                String configCacheJson = JSONObject.toJSON(configList).toString();
                redisUtil.set(cacheKey, configCacheJson, 10, TimeUnit.MINUTES);
            }
        }

        return Result.success("获取成功", configList);
    }

    /**
     * 修改基础配置信息
     *
     * @param param
     * @return
     */
    @Override
    public Result updateBasicConfig(ConfigParam param) {
        List<Config> configData = param.getConfigData();
        List<String> basicKeyList = new ArrayList<String>(Arrays.asList("logo", "name", "version", "miitbeian",
                "login_page_pic", "org_name", "address", "phone", "email"));
        for (Config item : configData) {
            String key = item.getKey();
            String value = item.getValue();
            if (StringUtil.isBlank(key) || StringUtil.isBlank(value)) {
                return Result.error("配置标识或配置值不能为空");
            }
            if (!basicKeyList.contains(key)) {
                return Result.error("配置标识错误");
            }
        }

        //更新配置信息
        for (Config item : configData) {
            configMapper.updateConfigValueByKey(item.getKey(), item.getValue());
        }

        //删除配置缓存信息
        String cacheKey = getConfigCacheKey(1);
        redisUtil.del(cacheKey);

        return Result.success("操作成功");
    }

    /**
     * 获取配置信息缓存key
     *
     * @param type
     * @return
     */
    public String getConfigCacheKey(Integer type) {
        return Constant.BASIC_CONFIG_CACHE_PREFIX + type;
    }
}