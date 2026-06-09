package com.zemcho.ddql.service.wechat.shop;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.wechat.shop.param.WechatShopDetailParam;

public interface ConsumptionShopService {

    Result selectList(SearchParam param, String token);

    /**
     * 获取商家详情（包含用币规则）
     *
     * @param param 请求参数
     * @return 商家详情
     */
    Result getShopDetail(WechatShopDetailParam param);
}
