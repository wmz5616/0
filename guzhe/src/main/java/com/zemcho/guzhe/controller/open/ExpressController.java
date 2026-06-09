package com.zemcho.guzhe.controller.open;

import com.kuaidi100.sdk.response.SubscribeResp;
import com.zemcho.guzhe.service.open.ExpressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @title: ExpressController
 * @Description:
 * @Date: 2025/10/20 16:21
 */
@RestController
@RequestMapping("/open/express")
public class ExpressController {
    @Autowired
    ExpressService service;

    /**
     * 快递100订阅回调
     *
     * @param param
     * @return
     */
    @RequestMapping("/subscribe/callback")
    public SubscribeResp subscribeCallback(String sign, String param) {
        return service.subscribeCallback(sign, param);
    }

}
