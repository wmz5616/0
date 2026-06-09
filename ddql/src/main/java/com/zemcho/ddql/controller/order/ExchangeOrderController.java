package com.zemcho.ddql.controller.order;

import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.order.param.OrderAuditParam;
import com.zemcho.ddql.controller.order.param.OrderRefundParam;
import com.zemcho.ddql.service.order.ExchangeOrderService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @title: ExchangeOrderController
 * @Description:
 * @Date: 2025/10/14 13:51
 */
@RestController
@RequestMapping("/order/exchange")
public class ExchangeOrderController {
    @Autowired
    ExchangeOrderService service;

    /**
     * 兑换订单列表
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
     * 兑换订单统计信息
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
     * 兑换订单数据导出
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
     * 兑换订单详情
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
     * 兑换订单-未发货数据导出
     *
     * @param param
     * @param result
     * @param response
     */
    @RequestMapping("/un_dispatched/export")
    public void orderUnDispatchedExport(@Validated @RequestBody SearchParam param, BindingResult result,
                                        HttpServletResponse response) {
        service.orderUnDispatchedExport(param, response);
    }

    /**
     * 兑换订单-导入物流单号
     *
     * @param file
     * @return
     */
    @PostMapping("/express_no/import")
    public Result importExpressNo(@RequestParam("file") MultipartFile file) {
        return service.importExpressNo(file);
    }

    /**
     * 兑换订单-退货
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @Log(description = "退货", module = "兑换订单")
    @RequestMapping("/refund")
    public Result orderRefund(@Validated @RequestBody OrderRefundParam param, BindingResult result,
                              @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.orderRefund(param, token);
    }

    /**
     * 兑换订单-退货申请列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/refund/apply/lists")
    public Result orderRefundApplyLists(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.orderRefundApplyLists(param);
    }

    /**
     * 兑换订单-退货申请详情
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/refund/apply/info")
    public Result orderRefundApplyInfo(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.orderRefundApplyInfo(param);
    }

    /**
     * 兑换订单-退货审核
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @Log(description = "审核", module = "退货审核")
    @RequestMapping("/refund/audit")
    public Result orderRefundAudit(@Validated @RequestBody OrderAuditParam param, BindingResult result,
                                   @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.orderRefundAudit(param, token);
    }
}
