package com.zemcho.guzhe.service.sys.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.sys.param.TransactionFlowSearchParam;
import com.zemcho.guzhe.controller.sys.vo.*;
import com.zemcho.guzhe.mapper.sys.TransactionFlowMapper;
import com.zemcho.guzhe.service.sys.TransactionFlowService;
import com.zemcho.guzhe.util.excel.*;
import com.alibaba.excel.write.handler.WriteHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 交易流水服务实现类
 */
@Service
public class ITransactionFlowService implements TransactionFlowService {

    @Autowired
    private TransactionFlowMapper flowMapper;

    @Override
    public Result selectList(TransactionFlowSearchParam param) {
        int pageNum = (param.getPageNum() <= 0) ? 1 : param.getPageNum();
        int pageSize = (param.getPageSize() <= 0) ? 10 : param.getPageSize();

        long total = flowMapper.selectTransactionFlowSnapshotList_COUNT(param);

        PageHelper.startPage(pageNum, pageSize, false);
        List<TransactionFlowVo> list = flowMapper.selectTransactionFlowSnapshotList(param);

        PageInfo<TransactionFlowVo> pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(total);

        return Result.success("获取成功", pageInfo);
    }

    @Override
    public Result selectSummaryList(TransactionFlowSearchParam param) {
        int pageNum = (param.getPageNum() <= 0) ? 1 : param.getPageNum();
        int pageSize = (param.getPageSize() <= 0) ? 10 : param.getPageSize();

        long total = flowMapper.selectTransactionSummarySnapshotList_COUNT(param);

        PageHelper.startPage(pageNum, pageSize, false);
        List<TransactionSummaryVo> list = flowMapper.selectTransactionSummarySnapshotList(param);

        PageInfo<TransactionSummaryVo> pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(total);

        return Result.success("获取成功", pageInfo);
    }

    @Override
    public Result getSummary(TransactionFlowSearchParam param) {
        TransactionFlowSummaryVo summary = flowMapper.selectTransactionFlowSnapshotSummary(param);
        if (summary == null) {
            summary = new TransactionFlowSummaryVo();
            summary.setTotalCount(0L);
        }
        return Result.success("获取成功", summary);
    }

    @Override
    public void exportRecord(TransactionFlowSearchParam param, HttpServletResponse response) {
        // 如果传入了选中的记录ID列表，则只导出选中的记录
        List<TransactionFlowVo> list;
        TransactionFlowSummaryVo summary;

        if (param.getSearchStrList() != null && !param.getSearchStrList().isEmpty()) {
            // 只查询选中的记录
            list = flowMapper.selectTransactionFlowSnapshotList(param);
            // 选中记录的统计需要重新计算
            summary = new TransactionFlowSummaryVo();
            summary.setTotalCount((long) list.size());
            long incomeCount = list.stream().filter(vo -> "收款".equals(vo.getType())).count();
            summary.setIncomeCount(incomeCount);
            summary.setExpenseCount(list.size() - incomeCount);
            BigDecimal incomeAmount = list.stream()
                    .filter(vo -> "收款".equals(vo.getType()))
                    .map(TransactionFlowVo::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal expenseAmount = list.stream()
                    .filter(vo -> "退款".equals(vo.getType()))
                    .map(TransactionFlowVo::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            // 计算手续费总计
            BigDecimal totalServiceFee = list.stream()
                    .map(TransactionFlowVo::getServiceFee)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            summary.setIncomeAmount(incomeAmount);
            summary.setExpenseAmount(expenseAmount);
            summary.setTotalServiceFee(totalServiceFee);
        } else {
            // 导出所有记录
            list = flowMapper.selectTransactionFlowSnapshotList(param);
            summary = flowMapper.selectTransactionFlowSnapshotSummary(param);
        }

        if (summary == null) {
            summary = new TransactionFlowSummaryVo();
        }

        // 转换为导出专用的 VO (8 列格式)
        List<TransactionFlowExportVo> exportList = list.stream().map(vo -> {
            TransactionFlowExportVo exportVo = new TransactionFlowExportVo();
            BeanUtils.copyProperties(vo, exportVo);
            // 交易流水格式中的金额列区分收入支出
            if ("收款".equals(vo.getType())) {
                exportVo.setIncome(vo.getAmount());
                exportVo.setExpense(BigDecimal.ZERO);
            } else {
                exportVo.setIncome(BigDecimal.ZERO);
                exportVo.setExpense(vo.getAmount());
            }
            return exportVo;
        }).collect(Collectors.toList());

        List<WriteHandler> handlers = Collections.singletonList(new TransactionFlowRecordHeaderHandler(summary));
        ExcelUtil.exportToWeb(response, exportList, "交易流水", "交易流水", TransactionFlowExportVo.class, handlers);
    }

    @Override
    public void exportDetail(TransactionFlowSearchParam param, HttpServletResponse response) {
        // 如果传入了选中的记录ID列表，则只导出选中的记录
        List<TransactionFlowVo> list;
        TransactionFlowSummaryVo summary;

        if (param.getSearchStrList() != null && !param.getSearchStrList().isEmpty()) {
            // 只查询选中的记录
            list = flowMapper.selectTransactionFlowSnapshotList(param);
            // 选中记录的统计需要重新计算
            summary = new TransactionFlowSummaryVo();
            summary.setTotalCount((long) list.size());
            long incomeCount = list.stream().filter(vo -> "收款".equals(vo.getType())).count();
            summary.setIncomeCount(incomeCount);
            summary.setExpenseCount(list.size() - incomeCount);
            BigDecimal incomeAmount = list.stream()
                    .filter(vo -> "收款".equals(vo.getType()))
                    .map(TransactionFlowVo::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal expenseAmount = list.stream()
                    .filter(vo -> "退款".equals(vo.getType()))
                    .map(TransactionFlowVo::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            // 计算手续费总计
            BigDecimal totalServiceFee = list.stream()
                    .map(TransactionFlowVo::getServiceFee)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            summary.setIncomeAmount(incomeAmount);
            summary.setExpenseAmount(expenseAmount);
            summary.setTotalServiceFee(totalServiceFee);
        } else {
            // 导出所有记录
            list = flowMapper.selectTransactionFlowSnapshotList(param);
            summary = flowMapper.selectTransactionFlowSnapshotSummary(param);
        }

        if (summary == null) {
            summary = new TransactionFlowSummaryVo();
        }
        // 设置商户号
        summary.setMerchantNo("10090719235");

        List<WriteHandler> handlers = Collections.singletonList(new TransactionFlowHeaderHandler(summary, param));
        ExcelUtil.exportToWeb(response, list, "交易明细", "交易明细", TransactionFlowVo.class, handlers);
    }

    @Override
    public void exportSummary(TransactionFlowSearchParam param, HttpServletResponse response) {
        List<TransactionSummaryVo> list = flowMapper.selectTransactionSummarySnapshotList(param);

        if (list != null && !list.isEmpty()) {
            TransactionSummaryVo total = new TransactionSummaryVo();
            total.setBillDate("合计");
            total.setTransactionIncome(list.stream().map(TransactionSummaryVo::getTransactionIncome).reduce(BigDecimal.ZERO, BigDecimal::add));
            total.setRefundExpense(list.stream().map(TransactionSummaryVo::getRefundExpense).reduce(BigDecimal.ZERO, BigDecimal::add));
            total.setTransactionFeeExpense(list.stream().map(TransactionSummaryVo::getTransactionFeeExpense).reduce(BigDecimal.ZERO, BigDecimal::add));
            total.setTransactionFeeReturn(list.stream().map(TransactionSummaryVo::getTransactionFeeReturn).reduce(BigDecimal.ZERO, BigDecimal::add));
            list.add(total);
        }

        ExcelUtil.exportToWeb(response, list, "交易汇总", "交易汇总", TransactionSummaryVo.class);
    }

    @Override
    public Result selectSubLedgerSummaryList(TransactionFlowSearchParam param) {
        int pageNum = (param.getPageNum() <= 0) ? 1 : param.getPageNum();
        int pageSize = (param.getPageSize() <= 0) ? 10 : param.getPageSize();

        long total = flowMapper.selectSubLedgerSummarySnapshotList_COUNT(param);

        PageHelper.startPage(pageNum, pageSize, false);
        List<SubLedgerSummaryVo> list = flowMapper.selectSubLedgerSummarySnapshotList(param);

        PageInfo<SubLedgerSummaryVo> pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(total);

        return Result.success("获取成功", pageInfo);
    }

    @Override
    public void exportSubLedgerSummary(TransactionFlowSearchParam param, HttpServletResponse response) {
        List<SubLedgerSummaryVo> list = flowMapper.selectSubLedgerSummarySnapshotList(param);
        if (list != null && !list.isEmpty()) {
            SubLedgerSummaryVo total = new SubLedgerSummaryVo();
            total.setBillDate("合计");
            total.setDivideAmount(list.stream().map(SubLedgerSummaryVo::getDivideAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            total.setRefundAmount(list.stream().map(SubLedgerSummaryVo::getRefundAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            list.add(total);
        }

        ExcelUtil.exportToWeb(response, list, "分账汇总", "分账汇总", SubLedgerSummaryVo.class);
    }

    @Override
    public Result selectSubLedgerDetailList(TransactionFlowSearchParam param) {
        int pageNum = (param.getPageNum() <= 0) ? 1 : param.getPageNum();
        int pageSize = (param.getPageSize() <= 0) ? 10 : param.getPageSize();

        long total = flowMapper.selectSubLedgerDetailList_COUNT(param);

        PageHelper.startPage(pageNum, pageSize, false);
        List<com.zemcho.guzhe.controller.sys.vo.SubLedgerDetailVo> list = flowMapper.selectSubLedgerDetailList(param);

        PageInfo<com.zemcho.guzhe.controller.sys.vo.SubLedgerDetailVo> pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(total);

        return Result.success("获取成功", pageInfo);
    }

    @Override
    public Result getSubLedgerDetailSummary(TransactionFlowSearchParam param) {
        return Result.success("获取成功", flowMapper.selectSubLedgerDetailSummary(param));
    }

    @Override
    public void exportSubLedgerDetail(TransactionFlowSearchParam param, HttpServletResponse response) {
        List<SubLedgerDetailVo> list = flowMapper.selectSubLedgerDetailList(param);
        SubLedgerDetailSummaryVo summary = flowMapper.selectSubLedgerDetailSummary(param);
        if (summary == null) {
            summary = new SubLedgerDetailSummaryVo();
        }
        List<WriteHandler> handlers = Collections.singletonList(new SubLedgerDetailHeaderHandler(summary, param));
        ExcelUtil.exportToWeb(response, list, "分账明细", "分账明细", SubLedgerDetailVo.class, handlers);
    }
}
