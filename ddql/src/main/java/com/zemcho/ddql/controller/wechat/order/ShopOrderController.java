package com.zemcho.ddql.controller.wechat.order;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.wechat.order.param.ShopOrderRefundParam;
import com.zemcho.ddql.controller.wechat.order.param.WechatShopOrderParam;
import com.zemcho.ddql.controller.wechat.shop.param.ShopOrderCreateParam;
import com.zemcho.ddql.service.wechat.order.ShopOrderService;
import com.zemcho.ddql.util.tgy.dto.WxJsPayCallBackResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author HXH
 */
//交易记录
@RestController
@RequestMapping("/wechat/shop/order")
public class ShopOrderController {
    @Autowired
    private ShopOrderService shopOrderService;

    /**
     * 查询商家交易记录
     *
     * @param result         参数校验结果
     * @param token          token
     * @return result
     */
    @RequestMapping("/select")
    public Result select(@Validated @RequestBody WechatShopOrderParam param, BindingResult result,
                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopOrderService.select(param, token);
    }

    /**
     * 导出商家订单数据
     *
     * @param param
     * @param result
     * @param response
     * @param token
     */
    @RequestMapping("/export")
    public void orderExport(@Validated @RequestBody WechatShopOrderParam param, BindingResult result,
                            HttpServletResponse response, @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            throw new IllegalArgumentException(result.getFieldError().getDefaultMessage());
        }
        shopOrderService.businessDataExport(param, response, token);
    }
    /**
     * 查询商家交易记录详情
     *
     * @param result         参数校验结果
     * @param token          token
     * @return result
     */
    @RequestMapping("/detail/select")
    public Result selectDetail(@Validated @RequestBody SearchParam param, BindingResult result,
                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopOrderService.selectDetail(param, token);
    }

    /**
     * 订单退款
     *
     * @param result         参数校验结果
     * @param token          token
     * @return result
     */
    @RequestMapping("/refund")
    public Result refund(@Validated @RequestBody ShopOrderRefundParam param, BindingResult result,
                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopOrderService.refund(param, token);
    }

    /**
     * 获取扫码支付订单支付配置信息
     *
     * @param param 支付参数
     * @param result 参数校验结果
     * @param token token
     * @return result
     */
    @RequestMapping("/pay")
    public Result pay(@Validated @RequestBody SearchParam param, BindingResult result,
                      @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopOrderService.pay(param, token);
    }

    /**
     * ShopOrder 支付回调接口
     */
    @RequestMapping("/payCallBack")
    public String shopOrderPayCallBack(@Validated @RequestBody WxJsPayCallBackResponse response) {
        return shopOrderService.shopOrderPayCallBack(response);
    }

}
