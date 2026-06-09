package com.zemcho.ddql.controller.sys;

import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.controller.sys.param.TransactionFlowSearchParam;
import com.zemcho.ddql.job.reconciliation.ReconciliationTasks;
import com.zemcho.ddql.service.sys.TransactionFlowService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 交易流水控制器
 */
@RestController
@RequestMapping("/transaction")
public class TransactionFlowController {

    @Autowired
    private TransactionFlowService flowService;

    @Autowired
    private ReconciliationTasks reconciliationTasks;

    /**
     * 获取交易流水列表 (明细)
     */
    @GetMapping("/flow/lists")
    @Log(description = "获取交易流水列表", module = "对账管理")
    public Result getLists(@ModelAttribute TransactionFlowSearchParam param) {
        return flowService.selectList(param);
    }

    /**
     * 获取交易明细列表
     */
    @GetMapping("/detail/lists")
    @Log(description = "获取交易明细列表", module = "对账管理")
    public Result getDetailLists(@ModelAttribute TransactionFlowSearchParam param) {
        return flowService.selectList(param);
    }

    /**
     * 获取交易流水汇总统计 (用于明细页 Alert)
     */
    @GetMapping("/flow/summary")
    @Log(description = "获取交易流水统计", module = "对账管理")
    public Result getSummary(TransactionFlowSearchParam param) {
        return flowService.getSummary(param);
    }

    /**
     * 获取交易汇总列表 (按日聚合)
     */
    @GetMapping("/summary/lists")
    @Log(description = "获取交易汇总列表", module = "对账管理")
    public Result getSummaryList(@ModelAttribute TransactionFlowSearchParam param) {
        return flowService.selectSummaryList(param);
    }

    /**
     * 导出交易汇总 Excel
     */
    @PostMapping("/summary/export")
    @Log(description = "导出交易汇总", module = "对账管理")
    public void exportSummary(@RequestBody TransactionFlowSearchParam param, HttpServletResponse response) {
        flowService.exportSummary(param, response);
    }

    /**
     * 获取分账汇总列表 (按日聚合)
     */
    @GetMapping("/subledger/summary/lists")
    @Log(description = "获取分账汇总列表", module = "对账管理")
    public Result getSubLedgerSummaryList(@ModelAttribute TransactionFlowSearchParam param) {
        return flowService.selectSubLedgerSummaryList(param);
    }

    /**
     * 导出分账汇总 Excel
     */
    @PostMapping("/subledger/summary/export")
    @Log(description = "导出分账汇总", module = "对账管理")
    public void exportSubLedgerSummary(@RequestBody TransactionFlowSearchParam param, HttpServletResponse response) {
        flowService.exportSubLedgerSummary(param, response);
    }

    /**
     * 导出交易流水 Excel (8 列格式)
     */
    @PostMapping("/flow/export")
    @Log(description = "导出交易流水", module = "对账管理")
    public void exportRecord(@RequestBody TransactionFlowSearchParam param, HttpServletResponse response) {
        flowService.exportRecord(param, response);
    }

    /**
     * 导出交易明细 Excel (13 列格式)
     */
    @PostMapping("/detail/export")
    @Log(description = "导出交易明细", module = "对账管理")
    public void exportDetail(@RequestBody TransactionFlowSearchParam param, HttpServletResponse response) {
        flowService.exportDetail(param, response);
    }

    /**
     * 获取分账明细列表
     */
    @GetMapping("/subledger/detail/lists")
    @Log(description = "获取分账明细列表", module = "对账管理")
    public Result getSubLedgerDetailList(@ModelAttribute TransactionFlowSearchParam param) {
        return flowService.selectSubLedgerDetailList(param);
    }

    /**
     * 获取分账明细统计
     */
    @GetMapping("/subledger/summary")
    @Log(description = "获取分账明细统计", module = "对账管理")
    public Result getSubLedgerDetailSummary(TransactionFlowSearchParam param) {
        return flowService.getSubLedgerDetailSummary(param);
    }

    /**
     * 导出分账明细 Excel
     */
    @PostMapping("/subledger/detail/export")
    @Log(description = "导出分账明细", module = "对账管理")
    public void exportSubLedgerDetail(@RequestBody TransactionFlowSearchParam param, HttpServletResponse response) {
        flowService.exportSubLedgerDetail(param, response);
    }

    /**
     * 手动触发交易对账统计（默认统计昨天）
     */
    @PostMapping("/reconciliation/trigger")
    @Log(description = "手动触发对账统计", module = "对账管理")
    public Result triggerReconciliation(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate targetDate) {
        try {
            LocalDate date = targetDate != null ? targetDate : LocalDate.now().minusDays(1);
            reconciliationTasks.transactionReconciliationTask(date);
            reconciliationTasks.subLedgerReconciliationTask(date);
            return Result.success("对账统计执行完成", null);
        } catch (Exception e) {
            return Result.error("对账统计执行失败：" + e.getMessage());
        }
    }
}
