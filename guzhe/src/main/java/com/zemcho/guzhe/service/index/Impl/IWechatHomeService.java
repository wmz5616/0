package com.zemcho.guzhe.service.index.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.entity.cas.CasUser;
import com.zemcho.guzhe.entity.sys.Config;
import com.zemcho.guzhe.entity.sys.HomeBanner;
import com.zemcho.guzhe.entity.sys.HomePageBanner;
import com.zemcho.guzhe.entity.sys.Notice;
import com.zemcho.guzhe.mapper.cas.CasUserMapper;
import com.zemcho.guzhe.mapper.sys.ConfigMapper;
import com.zemcho.guzhe.mapper.sys.HomeBannerMapper;
import com.zemcho.guzhe.mapper.sys.HomePageBannerMapper;
import com.zemcho.guzhe.mapper.sys.NoticeMapper;
import com.zemcho.guzhe.service.index.WechatHomeService;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author HXH
 */
@Service
public class IWechatHomeService implements WechatHomeService {

    @Autowired
    private HomePageBannerMapper homePageBannerMapper;
    @Autowired
    private NoticeMapper noticesMapper;
    @Autowired
    private HomeBannerMapper homeBannerMapper;
    @Autowired
    private ConfigMapper configMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CasUserMapper casUserMapper;
    @Override
    public Result selectShowLists() {
        List<HomePageBanner> list = homePageBannerMapper.selectShowLists();
        return Result.success("查询成功", list);
    }

    @Override
    public Result getTopNotice() {
        List<Notice> notices = noticesMapper.selectTopNotice();
        return Result.success("获取成功", notices);
    }

    @Override
    public Result selectLists(String token) {
        List<HomeBanner> list;
        Integer sortType=null;
        if (token != null && !token.isEmpty()) {
            Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
            if (userId == null) {
                return Result.error("参数错误");
            }
            CasUser casUser = casUserMapper.selectById(userId);
            if (casUser == null) {
                return Result.error("用户不存在");
            }
            if(casUser.getAdminId()==null|| casUser.getAdminId() == 0){
                sortType=0;
                //普通用户
                list = homeBannerMapper.selectLists(sortType);
            }else {
                //管理员
                list = homeBannerMapper.selectLists(null);
            }
        }else {
            //未登录
            sortType=0;
            list = homeBannerMapper.selectLists(sortType);
        }

        return Result.success("操作成功", list);
    }

    @Override
    public Result selectbanner(String token) {
        List<HomeBanner> list;
        Integer sortType=null;
        if (token != null && !token.isEmpty()) {
            Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
            if (userId == null) {
                return Result.error("参数错误");
            }
            CasUser casUser = casUserMapper.selectById(userId);
            if (casUser == null) {
                return Result.error("用户不存在");
            }
            if(casUser.getAdminId()==null|| casUser.getAdminId() == 0){
                sortType=0;
                //普通用户
                list = homeBannerMapper.selectbanner(sortType);
            }else {
                //管理员
                list = homeBannerMapper.selectbanner(null);
            }
        }else {
            //未登录
            sortType=0;
            list = homeBannerMapper.selectbanner(sortType);
        }

        return Result.success("操作成功", list);
    }

    @Override
    public Result getBasicConfig() {
        return getConfigByType(1);
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
     * 获取配置信息缓存key
     *
     * @param type
     * @return
     */
    public String getConfigCacheKey(Integer type) {
        return Constant.BASIC_CONFIG_CACHE_PREFIX + type;
    }
}
