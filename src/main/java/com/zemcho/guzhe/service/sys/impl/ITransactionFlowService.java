package com.zemcho.guzhe.service.sys.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.sys.param.TransactionFlowSearchParam;
import com.zemcho.guzhe.controller.sys.vo.TransactionFlowSummaryVo;
import com.zemcho.guzhe.controller.sys.vo.TransactionFlowVo;
import com.zemcho.guzhe.mapper.sys.TransactionFlowMapper;
import com.zemcho.guzhe.service.sys.TransactionFlowService;
import com.zemcho.guzhe.util.excel.ExcelUtil;
import com.zemcho.guzhe.util.excel.TransactionFlowHeaderHandler;
import com.alibaba.excel.write.handler.WriteHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ITransactionFlowService implements TransactionFlowService {

    @Autowired
    private TransactionFlowMapper flowMapper;

    @Override
    public Result selectList(TransactionFlowSearchParam param) {
        int pageNum = (param.getPageNum() <= 0) ? 1 : param.getPageNum();
        int pageSize = (param.getPageSize() <= 0) ? 10 : param.getPageSize();

        // 1. 手动查询 Count，避开插件自动优化风险
        long total = flowMapper.selectTransactionFlowList_COUNT(param);

        // 2. 开启分页（设置 false 不让 PageHelper 自动 count）
        PageHelper.startPage(pageNum, pageSize, false);
        List<TransactionFlowVo> list = flowMapper.selectTransactionFlowList(param);

        PageInfo<TransactionFlowVo> pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(total);

        return Result.success("获取成功", pageInfo);
    }

    @Override
    public Result getSummary(TransactionFlowSearchParam param) {
        TransactionFlowSummaryVo summary = flowMapper.selectTransactionFlowSummary(param);
        if (summary == null) {
            summary = new TransactionFlowSummaryVo();
            summary.setTotalCount(0L);
        }
        return Result.success("获取成功", summary);
    }

    @Override
    public void export(TransactionFlowSearchParam param, HttpServletResponse response) {
        // 1. 获取全量列表数据（不分页）
        List<TransactionFlowVo> list = flowMapper.selectTransactionFlowList(param);

        // 2. 获取当前筛选条件下的统计汇总
        TransactionFlowSummaryVo summary = flowMapper.selectTransactionFlowSummary(param);
        if (summary == null) {
            summary = new TransactionFlowSummaryVo();
            summary.setTotalCount(0L);
        }

        // 3. 核心变动：传入整个 summary 对象给 Handler，用于绘制复杂的网格表头
        List<WriteHandler> handlers = Collections.singletonList(new TransactionFlowHeaderHandler(summary));

        // 4. 调用工具类导出
        ExcelUtil.exportToWeb(response, list, "交易流水明细", "流水明细", TransactionFlowVo.class, handlers);
    }
}
