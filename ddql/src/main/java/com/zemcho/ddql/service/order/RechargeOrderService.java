package com.zemcho.ddql.service.order;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.order.param.OrderRefundParam;
import jakarta.servlet.http.HttpServletResponse;

public interface RechargeOrderService {
    /**
     * 充值订单列表
     *
     * @param param
     * @return
     */
    Result orderLists(SearchParam param);

    /**
     * 充值订单统计信息
     *
     * @param param
     * @return
     */
    Result orderStat(SearchParam param);

    /**
     * 充值订单数据导出
     *
     * @param param
     * @param response
     */
    void orderExport(SearchParam param, HttpServletResponse response);

    /**
     * 充值订单详情
     *
     * @param param
     * @return
     */
    Result orderInfo(SearchParam param);

    /**
     * 充值订单退款
     *
     * @param param
     * @param token
     * @return
     */
    Result orderRefund(OrderRefundParam param, String token);
}
