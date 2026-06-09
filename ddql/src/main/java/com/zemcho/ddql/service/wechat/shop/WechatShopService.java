package com.zemcho.ddql.service.wechat.shop;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.wechat.shop.param.WechatShopParam;

public interface WechatShopService {

    /**
     * 获取打卡店铺列表
     *
     * @param param
     * @return
     */
    Result selectList(WechatShopParam param);

    /**
     * 获取商圈下的店铺列表
     *
     * @param param
     * @return
     */
    Result getCircleShopList(SearchParam param);

    /**
     * 店铺点击次数+1
     *
     * @param param
     * @return
     */
    Result shopClickCountInc(SearchParam param);

    Result updateContract(SearchParam param, String token);

    Result getBusinessData(SearchParam param, String token);
}
