package com.zemcho.guzhe.controller.screen_order;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.screen_order.param.ScreenOrderManageAuditParam;
import com.zemcho.guzhe.controller.screen_order.param.ScreenOrderManageCancelParam;
import com.zemcho.guzhe.controller.screen_order.param.ScreenOrderManageInfoParam;
import com.zemcho.guzhe.controller.screen_order.param.ScreenOrderManageListParam;
import com.zemcho.guzhe.service.screen_order.ScreenOrderManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 后台店位订单管理
 */
@RestController
@RequestMapping("/screen_order")
public class ScreenOrderManageController {
    @Autowired
    private ScreenOrderManageService service;

    /**
     * 获取后台店位订单列表
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/lists")
    public Result lists(@Validated @RequestBody ScreenOrderManageListParam param, BindingResult result,
                        @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return service.lists(param, token);
    }

    /**
     * 获取后台店位订单详情
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/info")
    public Result info(@Validated @RequestBody ScreenOrderManageInfoParam param, BindingResult result,
                       @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return service.info(param, token);
    }

    /**
     * 后台审核店位订单
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/audit")
    public Result audit(@Validated @RequestBody ScreenOrderManageAuditParam param, BindingResult result,
                        @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return service.audit(param, token);
    }

    /**
     * 后台撤销店位订单
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/cancel")
    public Result cancel(@Validated @RequestBody ScreenOrderManageCancelParam param, BindingResult result,
                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return service.cancel(param, token);
    }
}
