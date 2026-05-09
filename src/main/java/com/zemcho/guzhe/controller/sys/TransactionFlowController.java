package com.zemcho.guzhe.controller.sys;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.sys.param.TransactionFlowSearchParam;
import com.zemcho.guzhe.service.sys.TransactionFlowService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 交易流水控制器
 */
@RestController
@RequestMapping("/transaction/flow")
public class TransactionFlowController {

    @Autowired
    private TransactionFlowService flowService;

    /**
     * 获取交易流水列表
     * 注意：GET 请求不使用 @RequestBody
     */
    @GetMapping("/lists")
    public Result getLists(@ModelAttribute TransactionFlowSearchParam param) {
        return flowService.selectList(param);
    }

    /**
     * 获取交易流水汇总统计
     * 注意：GET 请求不使用 @RequestBody
     */
    @GetMapping("/summary")
    public Result getSummary(TransactionFlowSearchParam param) {
        return flowService.getSummary(param);
    }

    /**
     * 导出交易流水 Excel
     * 导出通常使用 POST 配合 @RequestBody
     */
    @PostMapping("/export")
    public void export(@RequestBody TransactionFlowSearchParam param, HttpServletResponse response) {
        flowService.export(param, response);
    }
}
