package com.zemcho.ddql.controller.order;

import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.order.param.OrderRefundParam;
import com.zemcho.ddql.service.order.RechargeOrderService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @title: RechargeOrderController
 * @Description:
 * @Date: 2025/10/24 14:45
 */
@RestController
@RequestMapping("/order/recharge")
public class RechargeOrderController {
    @Autowired
    RechargeOrderService service;

    /**
     * 充值订单列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/lists")
    public Result orderLists(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.orderLists(param);
    }

    /**
     * 充值订单统计信息
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/stat")
    public Result orderStat(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.orderStat(param);
    }

    /**
     * 充值订单数据导出
     *
     * @param param
     * @param result
     * @param response
     */
    @RequestMapping("/export")
    public void orderExport(@Validated @RequestBody SearchParam param, BindingResult result,
                            HttpServletResponse response) {
        service.orderExport(param, response);
    }

    /**
     * 充值订单详情
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/info")
    public Result orderInfo(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.orderInfo(param);
    }

    /**
     * 充值订单退款
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @Log(description = "退款", module = "充值记录")
    @RequestMapping("/refund")
    public Result orderRefund(@Validated @RequestBody OrderRefundParam param, BindingResult result,
                              @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.orderRefund(param, token);
    }
}
