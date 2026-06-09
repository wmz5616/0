package com.zemcho.ddql.service.business;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.param.ShopManagerParam;

/**
 * 店铺管理者服务接口
 */
public interface ShopManagerService {

    /**
     * 新增店铺管理者
     * @param param 参数对象
     * @return 结果
     */
    Result addShopManager(ShopManagerParam param,String token,Boolean isWechat);

    /**
     * 更新店铺管理者
     * @param param 参数对象
     * @return 结果
     */
    Result updateShopManager(ShopManagerParam param);

    /**
     * 删除店铺管理者
     * @param param 删除参数
     * @return 结果
     */
    Result deleteShopManager(DeleteParam param,String token,Boolean isWechat);

    /**
     * 根据商家ID查询店铺管理者列表
     * @param param 查询参数(包含shopId)
     * @return 结果
     */
    Result getByShopId(SearchParam param,String token,Boolean isWechat);

    /**
     * 验证微信用户是否为指定店铺的管理者
     *
     * @param token
     * @param shopId
     * @return
     */
    Result checkWechatUserIsShopManager(String token, Integer shopId);
}