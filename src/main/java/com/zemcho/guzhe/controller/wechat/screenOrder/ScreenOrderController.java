package com.zemcho.guzhe.controller.wechat.screenOrder;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderInfoParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderListParam;
import com.zemcho.guzhe.service.wechat.screenOrder.ScreenOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店位订单
 */
@RestController
@RequestMapping("/wechat/screen_order")
public class ScreenOrderController {
    @Autowired
    private ScreenOrderService service;

    /**
     * 获取店位订单列表
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/lists")
    public Result lists(@Validated @RequestBody ScreenOrderListParam param, BindingResult result,
                        @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.lists(param, token);
    }

    /**
     * 获取店位订单详情
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/info")
    public Result info(@Validated @RequestBody ScreenOrderInfoParam param, BindingResult result,
                       @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.info(param, token);
    }
}
