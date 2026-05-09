package com.zemcho.guzhe.service.wechat.screen;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.wechat.screen.param.ScreenRentalAvailableParam;
import com.zemcho.guzhe.controller.wechat.screen.param.ScreenRentalRentParam;

public interface ScreenRentalService {
    /**
     * 获取可租用店位列表
     *
     * @param param
     * @return
     */
    Result availableLists(ScreenRentalAvailableParam param);

    /**
     * 屏幕店位租用
     *
     * @param param
     * @param token
     * @return
     */
    Result rent(ScreenRentalRentParam param, String token);
}
