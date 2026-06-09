package com.zemcho.ddql.service.order;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import jakarta.servlet.http.HttpServletResponse;

public interface WithdrawalOrderService {
    /**
     * 获取提现列表
     *
     * @param param
     * @return
     */
    Result withdrawalLists(SearchParam param);

    /**
     * 导出提现数据
     *
     * @param param
     * @param response
     */
    void withdrawalExport(SearchParam param, HttpServletResponse response);

    /**
     * 获取提现详情
     *
     * @param param
     * @return
     */
    Result withdrawalInfo(SearchParam param);
}
