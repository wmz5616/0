package com.zemcho.ddql.controller.wechat.personalCenter;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.controller.wechat.personalCenter.param.WechatShopOrderInfoParam;
import com.zemcho.ddql.controller.wechat.personalCenter.param.WechatShopOrderListParam;
import com.zemcho.ddql.service.wechat.personalCenter.WechatShopOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/wechat/personalCenter/shopOrder")
public class WechatShopOrderController {
    @Autowired
    private WechatShopOrderService wechatShopOrderService;

    /**
     * 查询门店订单列表
     */
    @RequestMapping("/lists")
    public Result orderList(@Valid @RequestBody WechatShopOrderListParam param, BindingResult result,
                            @RequestHeader("token") String token) {
        // 先做入参校验，避免无效参数进入业务层。
        if (result.hasErrors()) {
            return Result.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        // 列表查询包含用户身份校验、默认时间范围补全和分页查询。
        return wechatShopOrderService.orderList(param, token);
    }

    /**
     * 查询门店订单详情
     */
    @RequestMapping("/info")
    public Result orderInfo(@Valid @RequestBody WechatShopOrderInfoParam param, BindingResult result,
                            @RequestHeader("token") String token) {
        // 详情接口必须带订单ID，先做基础参数校验。
        if (result.hasErrors()) {
            return Result.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        // 业务层会继续校验当前用户是否有权查看该订单。
        return wechatShopOrderService.orderInfo(param, token);
    }
}
