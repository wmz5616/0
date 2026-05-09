package com.zemcho.guzhe.service.wechat.screenOrder;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderInfoParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderListParam;

public interface ScreenOrderService {
    /**
     * 获取店位订单列表
     *
     * @param param
     * @param token
     * @return
     */
    Result lists(ScreenOrderListParam param, String token);

    /**
     * 获取店位订单详情
     *
     * @param param
     * @param token
     * @return
     */
    Result info(ScreenOrderInfoParam param, String token);
}
