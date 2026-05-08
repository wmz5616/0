package com.zemcho.guzhe.service.open;

import com.kuaidi100.sdk.response.SubscribeResp;

public interface ExpressService {
    /**
     * 快递100订阅回调
     *
     * @param param
     * @return
     */
    SubscribeResp subscribeCallback(String sign, String param);
}
