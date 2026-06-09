package com.zemcho.ddql.service.app;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.app.param.AppAuthParam;
import com.zemcho.ddql.controller.app.param.AppVersionUpdateParam;

public interface AppService {
    /**
     * app端获取AccessToken
     *
     * @param param
     * @return
     */
    Result getAccessToken(AppAuthParam param);

    /**
     * 获取设备海报列表
     *
     * @param accessToken
     * @return
     */
    Result getPosterLists(String accessToken);

    /**
     * 获取设备详情
     *
     * @param accessToken
     * @return
     */
    Result getEquipmentInfo(String accessToken);

    /**
     * 获取最新版本信息
     *
     * @param accessToken
     * @return
     */
    Result latestVersionInfo(String accessToken);

    /**
     * 更新设备版本信息
     *
     * @param param
     * @param accessToken
     * @return
     */
    Result updateVersion(AppVersionUpdateParam param, String accessToken);

    /**
     * 获取签到配置信息
     *
     * @param accessToken
     * @return
     */
    Result getCheckInConfig(String accessToken);

    /**
     * 获取打卡店铺列表
     *
     * @param accessToken
     * @return
     */
    Result getShopLists(SearchParam param, String accessToken);

    /**
     * 获取用户打卡信息
     *
     * @param accessToken
     * @return
     */
    Result getUserCheckInInfo(String accessToken);
}
