package com.zemcho.guzhe.controller.order;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.order.param.OrderAuditParam;
import com.zemcho.guzhe.controller.order.param.OrderRefundParam;
import com.zemcho.guzhe.service.order.ProductOrderService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @title: ProductOrderController
 * @Description:
 * @Date: 2026/4/30 14:26
 */
@RestController
@RequestMapping("/product_order")
public class ProductOrderController {
    @Autowired
    private ProductOrderService service;

    /**
     * 获取商品订单列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/lists")
    public Result productOrderLists(@Validated @RequestBody SearchParam param, BindingResult result,
                                    @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.productOrderLists(param, token, false);
    }

    /**
     * 获取商品订单统计信息
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/stat")
    public Result productOrderStat(@Validated @RequestBody SearchParam param, BindingResult result,
                                   @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.productOrderStat(param, token, false);
    }

    /**
     * 导出商品订单数据
     *
     * @param param
     * @param result
     * @param response
     */
    @RequestMapping("/export")
    public void productOrderExport(@Validated @RequestBody SearchParam param, BindingResult result,
                                   HttpServletResponse response, @RequestHeader("token") String token) {
        service.productOrderExport(param, response, token, false);
    }

    /**
     * 获取商品订单详情
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/info")
    public Result productOrderInfo(@Validated @RequestBody SearchParam param, BindingResult result,
                                   @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.productOrderInfo(param, token, false);
    }

    /**
     * 商品订单--未发货数据导出
     *
     * @param param
     * @param result
     * @param response
     */
    @RequestMapping("/un_dispatched/export")
    public void productOrderUnDispatchedExport(@Validated @RequestBody SearchParam param, BindingResult result,
                                               HttpServletResponse response, @RequestHeader("token") String token) {
        service.productOrderUnDispatchedExport(param, response, token, false);
    }

    /**
     * 商品订单--导入物流单号
     *
     * @param file
     * @return
     */
    @PostMapping("/express_no/import")
    public Result importExpressNo(@RequestParam("file") MultipartFile file) {
        return service.importExpressNo(file);
    }

    /**
     * 商品订单--退款
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @Log(description = "退款", module = "商品订单")
    @RequestMapping("/refund")
    public Result orderRefund(@Validated @RequestBody OrderRefundParam param, BindingResult result,
                              @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.orderRefund(param, token, false);
    }

    /**
     * 获取商品订单退款申请列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/refund/apply/lists")
    public Result orderRefundApplyLists(@Validated @RequestBody SearchParam param, BindingResult result,
                                        @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.orderRefundApplyLists(param, token, false);
    }

    /**
     * 获取商品订单退款申请详情
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
     * 商品订单--退款审核
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @Log(description = "审核", module = "退款审核")
    @RequestMapping("/refund/audit")
    public Result orderRefundAudit(@Validated @RequestBody OrderAuditParam param, BindingResult result,
                                   @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.orderRefundAudit(param, token, false);
    }
}
