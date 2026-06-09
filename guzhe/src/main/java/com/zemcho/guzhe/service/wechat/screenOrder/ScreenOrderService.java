package com.zemcho.guzhe.service.wechat.screenOrder;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderDisplayUpdateParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderInfoParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderListParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderScreenshotQueryParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderScreenshotTaskParam;

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

    /**
     * 修改店位订单展示内容
     *
     * @param param
     * @param token
     * @return
     */
    Result updateDisplayType(ScreenOrderDisplayUpdateParam param, String token);

    /**
     * 发起设备截图任务
     *
     * @param param
     * @param token
     * @return
     */
    Result createScreenshotTask(ScreenOrderScreenshotTaskParam param, String token);

    /**
     * 查询设备截图结果
     *
     * @param param
     * @param token
     * @return
     */
    Result screenshotInfo(ScreenOrderScreenshotQueryParam param, String token);

    /**
     * 获取是否展示屏幕店租用合约
     *
     * @return
     */
    Result contractStatus();

    /**
     * 获取屏幕店租用合约内容
     *
     * @return
     */
    Result contractContent();
}
