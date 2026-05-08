package com.zemcho.guzhe.controller.wechat.user;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.wechat.user.param.ProductOrderAddParam;
import com.zemcho.guzhe.controller.wechat.user.param.ProductOrderRefundRefundParam;
import com.zemcho.guzhe.controller.wechat.user.param.ProductOrderUpdateBaseParam;
import com.zemcho.guzhe.service.wechat.user.UserProductOrderService;
import com.zemcho.guzhe.util.tgy.dto.WxJsPayCallBackResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @title: UserProductOrderController
 * @Description:
 * @Date: 2026/4/28 8:56
 */
@RestController
@RequestMapping("/wechat/product_order")
public class UserProductOrderController {
    @Autowired
    private UserProductOrderService service;

    /**
     * 添加商品订单
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/add")
    public Result addProductOrder(@Validated @RequestBody ProductOrderAddParam param, BindingResult result,
                                  @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.addProductOrder(param, token);
    }

    /**
     * 获取商品订单详情
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/info")
    public Result productOrderInfo(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.userProductOrderInfo(param);
    }

    /**
     * 更新商品订单基础信息
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/base/update")
    public Result updateProductOrderBase(@Validated @RequestBody ProductOrderUpdateBaseParam param,
                                         BindingResult result, @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.updateProductOrderBase(param, token);
    }

    /**
     * 获取商品订单支付配置信息
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/pay/config")
    public Result productOrderPayConfig(@Validated @RequestBody SearchParam param, BindingResult result,
                                        @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.productOrderPayConfig(param, token);
    }

    /**
     * 商品订单支付回调
     *
     * @param response
     * @return
     */
    @RequestMapping("/pay/callBack")
    public String productOrderPayCallBack(@Validated @RequestBody WxJsPayCallBackResponse response) {
        return service.productOrderPayCallBack(response);
    }

    /**
     * 获取商品订单列表
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/lists")
    public Result productOrderLists(@Validated @RequestBody SearchParam param, BindingResult result,
                                    @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.userProductOrderLists(param, token);
    }

    /**
     * 获取商品订单统计信息
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/stat")
    public Result productOrderStat(@Validated @RequestBody SearchParam param, BindingResult result,
                                   @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.userProductOrderStat(param, token);
    }

    /**
     * 商品订单退款申请
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/refund")
    public Result productOrderRefund(@Validated @RequestBody ProductOrderRefundRefundParam param, BindingResult result,
                                     @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.productOrderRefund(param, token);
    }
}
