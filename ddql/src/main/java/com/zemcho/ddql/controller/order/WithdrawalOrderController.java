package com.zemcho.ddql.controller.order;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.service.order.WithdrawalOrderService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @title: WithdrawalOrderController
 * @Description:
 * @Date: 2025/10/15 17:49
 */
@RestController
@RequestMapping("/order/withdrawal")
public class WithdrawalOrderController {
    @Autowired
    WithdrawalOrderService service;

    /**
     * 获取提现列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/lists")
    public Result withdrawalLists(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.withdrawalLists(param);
    }

    /**
     * 导出提现数据
     *
     * @param param
     * @param result
     * @param response
     */
    @RequestMapping("/export")
    public void withdrawalExport(@Validated @RequestBody SearchParam param, BindingResult result,
                                 HttpServletResponse response) {
        service.withdrawalExport(param, response);
    }

    /**
     * 获取提现详情
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/info")
    public Result withdrawalInfo(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.withdrawalInfo(param);
    }
}
