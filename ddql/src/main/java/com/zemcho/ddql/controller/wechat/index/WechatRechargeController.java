package com.zemcho.ddql.controller.wechat.index;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.wechat.index.param.RechargeOrderParam;
import com.zemcho.ddql.service.recharge.RechargeConfigService;
import com.zemcho.ddql.service.wechat.index.WechatRechargeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @title: WechatRechargeController
 * @Description:
 * @Date: 2025/10/10 17:49
 */
@RestController
@RequestMapping("/wechat/recharge")
public class WechatRechargeController {
    @Autowired
    WechatRechargeService service;

    @Autowired
    private RechargeConfigService rechargeConfigService;

    /**
     * 获取充值配置信息
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/config")
    public Result getRechargeConfig(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return rechargeConfigService.getRechargeConfigInfo(param);
    }

    /**
     * 获取充值活动列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/activity/lists")
    public Result selectActivityList(@Validated @RequestBody SearchParam param, BindingResult result,
                                     @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.selectActivityList(param, token);
    }

    /**
     * 添加充值订单
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/order/add")
    public Result addOrder(@Validated @RequestBody RechargeOrderParam param, BindingResult result,
                           @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.addOrder(param, token);
    }

    /**
     * 获取充值订单详情
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/order/info")
    public Result orderInfo(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.orderInfo(param);
    }

    /**
     * 获取充值订单支付配置信息
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/order/pay/config")
    public Result orderPayConfig(@Validated @RequestBody SearchParam param, BindingResult result,
                                 @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.orderPayConfig(param, token);
    }

    /**
     * 充值订单支付回调
     *
     * @param body
     * @param headers
     * @return
     */
    @RequestMapping("/order/pay/callback")
    public ResponseEntity<String> orderPayCallback(@RequestBody String body,
                                                   @RequestHeader Map<String, String> headers) {
        return service.orderPayCallback(body, headers);
    }

    /**
     * 获取充值订单列表
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/order/lists")
    public Result orderLists(@Validated @RequestBody SearchParam param, BindingResult result,
                             @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.orderLists(param, token);
    }

    /**
     * 获取充值订单统计数据
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/order/count")
    public Result orderCountData(@Validated @RequestBody SearchParam param, BindingResult result,
                                 @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.orderCountData(param, token);
    }

    /**
     * 充值订单数据导出到邮箱
     *
     * @param param
     * @param result
     * @param token
     * @param request
     * @return
     */
    @RequestMapping("/order/export")
    public Result orderExportToEmail(@Validated @RequestBody SearchParam param, BindingResult result,
                                     @RequestHeader("token") String token, HttpServletRequest request) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.orderExportToEmail(param, token, request);
    }
}
