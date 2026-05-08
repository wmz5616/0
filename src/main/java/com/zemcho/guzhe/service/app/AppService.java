package com.zemcho.guzhe.service.app;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.app.param.AppAuthParam;
import com.zemcho.guzhe.controller.app.param.AppVersionUpdateParam;
import com.zemcho.guzhe.controller.app.param.ProductOrderQRParam;

public interface AppService {
    /**
     * app端获取AccessToken
     *
     * @param param
     * @return
     */
    Result getAccessToken(AppAuthParam param);

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
     * 获取设备下的商家列表
     *
     * @param accessToken
     * @return
     */
    Result getShopList(String accessToken);

    /**
     * 获取设备下某商家的商品分类列表
     *
     * @param param
     * @param accessToken
     * @return
     */
    Result getProductCategoryList(SearchParam param, String accessToken);

    /**
     * 获取设备下某商家的商品列表
     *
     * @param param
     * @param accessToken
     * @return
     */
    Result getProductList(SearchParam param, String accessToken);

    /**
     * 获取商品详情
     *
     * @param param
     * @param accessToken
     * @return
     */
    Result getProductInfo(SearchParam param, String accessToken);

    /**
     * 获取商品订单小程序码
     *
     * @param param
     * @param accessToken
     * @return
     */
    Result getProductOrderQRCode(ProductOrderQRParam param, String accessToken);

    /**
     * 获取设备屏幕店位租赁订单小程序码
     *
     * @param accessToken
     * @return
     */
    Result getScreenOrderQRCode(String accessToken);

    /**
     * 获取设备下的商品订单详情
     *
     * @param accessToken
     * @return
     */
    Result getProductOrderInfo(String accessToken);
}
