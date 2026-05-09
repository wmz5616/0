package com.zemcho.guzhe.service.shop;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.shop.param.ShopManagerParam;
import com.zemcho.guzhe.controller.wechat.shop.param.WechatShopManagerParam;

/**
 * 店铺管理者服务接口
 */
public interface ShopManagerService {

    /**
     * 新增店铺管理者
     *
     * @param param 参数对象
     * @return 结果
     */
    Result addShopManager(ShopManagerParam param);

    /**
     * 更新店铺管理者
     *
     * @param param 参数对象
     * @return 结果
     */
    Result updateShopManager(ShopManagerParam param);

    /**
     * 删除店铺管理者
     *
     * @param param 删除参数
     * @return 结果
     */
    Result deleteShopManager(DeleteParam param, String token, Boolean isWechat);

    /**
     * 根据商家ID查询店铺管理者列表
     *
     * @param param 查询参数(包含shopId)
     * @return 结果
     */
    Result getByShopId(SearchParam param, String token, Boolean isWechat);

    /**
     * 验证微信用户是否为指定店铺的管理者
     *
     * @param token
     * @param shopId
     * @return
     */
    Result checkWechatUserIsShopManager(String token, Integer shopId);

    /**
     * 小程序端--新增商家管理者
     *
     * @param param
     * @param token
     * @return
     */
    Result addShopManagerByWechat(WechatShopManagerParam param, String token);
}