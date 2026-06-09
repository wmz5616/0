package com.zemcho.ddql.service.sys;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.controller.sys.param.TransactionFlowSearchParam;
import jakarta.servlet.http.HttpServletResponse;

public interface TransactionFlowService {

    Result selectList(TransactionFlowSearchParam param);

    Result selectSummaryList(TransactionFlowSearchParam param);

    Result getSummary(TransactionFlowSearchParam param);

    void exportRecord(TransactionFlowSearchParam param, HttpServletResponse response);

    void exportDetail(TransactionFlowSearchParam param, HttpServletResponse response);

    void exportSummary(TransactionFlowSearchParam param, HttpServletResponse response);

    Result selectSubLedgerSummaryList(TransactionFlowSearchParam param);

    void exportSubLedgerSummary(TransactionFlowSearchParam param, HttpServletResponse response);

    Result selectSubLedgerDetailList(TransactionFlowSearchParam param);

    Result getSubLedgerDetailSummary(TransactionFlowSearchParam param);

    void exportSubLedgerDetail(TransactionFlowSearchParam param, HttpServletResponse response);
}
