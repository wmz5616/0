package com.zemcho.guzhe.service.sys;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.sys.param.TransactionFlowSearchParam;
import jakarta.servlet.http.HttpServletResponse;

public interface TransactionFlowService {

    Result selectList(TransactionFlowSearchParam param);

    Result getSummary(TransactionFlowSearchParam param);

    void export(TransactionFlowSearchParam param, HttpServletResponse response);
}
