package com.zemcho.ddql.service.wechat.personalCenter;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.controller.wechat.personalCenter.param.WechatShopOrderInfoParam;
import com.zemcho.ddql.controller.wechat.personalCenter.param.WechatShopOrderListParam;

public interface WechatShopOrderService {
    Result orderList(WechatShopOrderListParam param, String token);

    Result orderInfo(WechatShopOrderInfoParam param, String token);
}
