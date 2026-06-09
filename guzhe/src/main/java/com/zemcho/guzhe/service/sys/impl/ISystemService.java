package com.zemcho.guzhe.service.sys.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.sys.param.ConfigParam;
import com.zemcho.guzhe.entity.sys.Config;
import com.zemcho.guzhe.mapper.sys.ConfigMapper;
import com.zemcho.guzhe.service.sys.SystemService;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.StringUtil;
import com.zemcho.guzhe.util.redis.RedisUtil;
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
        return getConfigByType(1);
    }

    /**
     * 获取屏幕店租用合约配置
     *
     * @param
     * @return
     */
    @Override
    public Result getRentalContractConfig() {
        String cacheKey = getConfigCacheKey(2);

        Object configObject = redisUtil.get(cacheKey);
        List<Config> allConfigList = null;
        if (configObject != null) {
            allConfigList = JSON.parseArray((String) configObject, Config.class);
        }

        if (allConfigList == null || allConfigList.isEmpty()) {
            allConfigList = configMapper.selectConfigListByType(2);
            if (allConfigList != null && !allConfigList.isEmpty()) {
                String configCacheJson = JSONObject.toJSON(allConfigList).toString();
                redisUtil.set(cacheKey, configCacheJson, 10, TimeUnit.MINUTES);
            }
        }

        // 过滤出只与屏幕店租用合约相关的配置
        List<String> rentalKeys = Arrays.asList("show_rental_contract", "rental_contract");
        List<Config> rentalConfigList = allConfigList.stream()
                .filter(config -> rentalKeys.contains(config.getKey()))
                .collect(java.util.stream.Collectors.toList());

        return Result.success("获取成功", rentalConfigList);
    }

    public Result getConfigByType(Integer configType) {
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
     * 通用配置更新方法
     *
     * @param param       配置参数
     * @param configType  配置类型：1-基础配置，2-合约配置
     * @param allowedKeys 允许的配置 key 列表
     * @param checkValue  是否校验 value 不能为空（基础配置需要，合约配置中 rental_contract 可为空）
     * @return
     */
    private Result updateConfigByType(ConfigParam param, Integer configType, List<String> allowedKeys, boolean checkValue) {
        List<Config> configData = param.getConfigData();

        // 基础校验
        for (Config item : configData) {
            String key = item.getKey();
            String value = item.getValue();

            if (StringUtil.isBlank(key)) {
                return Result.error("配置标识不能为空");
            }

            if (checkValue && StringUtil.isBlank(value)) {
                return Result.error("配置标识或配置值不能为空");
            }

            if (!allowedKeys.contains(key)) {
                return Result.error("配置标识错误");
            }
        }

        // 业务校验：合约配置特殊逻辑
        if (configType == 2) {
            Config showContract = configData.stream()
                    .filter(c -> "show_rental_contract".equals(c.getKey()))
                    .findFirst()
                    .orElse(null);

            if (showContract != null && "1".equals(showContract.getValue())) {
                boolean hasContract = configData.stream()
                        .anyMatch(c -> "rental_contract".equals(c.getKey())
                                && StringUtil.isNotBlank(c.getValue()));
                if (!hasContract) {
                    return Result.error("展示合约时，合约内容不能为空");
                }
            }
        }

        // 更新配置信息
        for (Config item : configData) {
            configMapper.updateConfigValueByKey(item.getKey(), item.getValue());
        }

        // 根据 type 动态删除对应的配置缓存
        String cacheKey = getConfigCacheKey(configType);
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

   /**
     * 修改基础配置信息
     *
     * @return
     */
    @Override
    public Result updateBasicConfig(ConfigParam param) {
        List<String> allowedKeys = Arrays.asList("logo", "name", "version", "miitbeian",
            "login_page_pic", "org_name", "address", "phone", "email","introduce");
        return updateConfigByType(param, 1, allowedKeys, false);
}
    /**
     * 修改屏幕店租用合约配置
     *
     * @return
     */
    @Override
    public Result updateRentalContractConfig(ConfigParam param) {
        List<String> allowedKeys = Arrays.asList("show_rental_contract", "rental_contract");
        return updateConfigByType(param, 2, allowedKeys, false);
    }

    /**
     * 获取登录页配图
     */
    @Override
    public Result getLoginPic() {
        String cacheKey = Constant.BASIC_CONFIG_CACHE_PREFIX + "login_page_pic";

        Object picUrl = redisUtil.get(cacheKey);
        if (picUrl != null) {
            return Result.success("获取成功", picUrl);
        }
        String key = "login_page_pic";
        Config config = configMapper.selectConfigByKey(key);
        if (config == null || StringUtil.isBlank(config.getValue())) {
            return Result.success("获取成功", "");
        }

        String picUrlValue = config.getValue();
        redisUtil.set(cacheKey, picUrlValue, 10, TimeUnit.MINUTES);

        return Result.success("获取成功", picUrlValue);
    }

    /**
     * 保存登录页配图
     */
    @Override
    public Result saveLoginPicConfig(ConfigParam param) {
        List<Config> configData = param.getConfigData();

        if (configData == null || configData.isEmpty()) {
            return Result.error("配置数据不能为空");
        }

        for (Config item : configData) {
            String key = item.getKey();
            String value = item.getValue();

            if (StringUtil.isBlank(key)) {
                return Result.error("配置标识不能为空");
            }

            if (!"login_page_pic".equals(key)) {
                return Result.error("配置标识错误，仅支持 login_page_pic");
            }

            if (StringUtil.isNotBlank(value)) {
                configMapper.updateConfigValueByKey(key, value);
            }
        }

        String cacheKey = Constant.BASIC_CONFIG_CACHE_PREFIX + "login_page_pic";
        redisUtil.del(cacheKey);

        return Result.success("操作成功");
    }

    @Override
    public Result getMerchantNoticeConfig() {
        List<String> allowedKeys = Arrays.asList("show_merchant_notice", "merchant_notice");
        return getMerchantNoticeConfigByKeys(allowedKeys);
    }

    /**
     * 根据 key 列表获取配置
     *
     * @param keys
     * @return
     */
    private Result getMerchantNoticeConfigByKeys(List<String> keys) {
        List<Config> configList = new ArrayList<>();
        for (String key : keys) {
            Config config = configMapper.selectConfigByKey(key);
            if (config != null) {
                configList.add(config);
            }
        }
        return Result.success("获取成功", configList);
    }

    @Override
    public Result updateMerchantNoticeConfig(ConfigParam param) {
        List<Config> configData = param.getConfigData();
        List<String> allowedKeys = Arrays.asList("show_merchant_notice", "merchant_notice");
        // 基础校验
        for (Config item : configData) {
            String key = item.getKey();
            String value = item.getValue();

            if (StringUtil.isBlank(key)) {
                return Result.error("配置标识不能为空");
            }

            if (!allowedKeys.contains(key)) {
                return Result.error("配置标识错误");
            }
        }
        // 业务校验：如果开启展示入驻前须知，则须知内容不能为空
        Config showNotice = configData.stream()
                .filter(c -> "show_merchant_notice".equals(c.getKey()))
                .findFirst()
                .orElse(null);

        if (showNotice != null && "1".equals(showNotice.getValue())) {
            boolean hasNotice = configData.stream()
                    .anyMatch(c -> "merchant_notice".equals(c.getKey())
                            && StringUtil.isNotBlank(c.getValue()));
            if (!hasNotice) {
                return Result.error("开启入驻前须知时，须知内容不能为空");
            }
        }
        // 更新配置信息
        for (Config item : configData) {
            configMapper.updateConfigValueByKey(item.getKey(), item.getValue());
        }
        // 清除缓存
        String cacheKey = getConfigCacheKey(2);
        redisUtil.del(cacheKey);

        return Result.success("操作成功");
    }
}