package com.zemcho.guzhe.service.screen_order;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.screen_order.param.ScreenOrderManageAuditParam;
import com.zemcho.guzhe.controller.screen_order.param.ScreenOrderManageCancelParam;
import com.zemcho.guzhe.controller.screen_order.param.ScreenOrderManageInfoParam;
import com.zemcho.guzhe.controller.screen_order.param.ScreenOrderManageListParam;

/**
 * 后台店位订单管理
 */
public interface ScreenOrderManageService {
    /**
     * 获取后台店位订单列表
     *
     * @param param
     * @param token
     * @return
     */
    Result lists(ScreenOrderManageListParam param, String token);

    /**
     * 获取后台店位订单详情
     *
     * @param param
     * @param token
     * @return
     */
    Result info(ScreenOrderManageInfoParam param, String token);

    /**
     * 后台审核店位订单
     *
     * @param param
     * @param token
     * @return
     */
    Result audit(ScreenOrderManageAuditParam param, String token);

    /**
     * 后台撤销店位订单
     *
     * @param param
     * @param token
     * @return
     */
    Result cancel(ScreenOrderManageCancelParam param, String token);
}
