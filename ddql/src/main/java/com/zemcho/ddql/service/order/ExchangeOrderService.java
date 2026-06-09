package com.zemcho.ddql.service.order;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.order.param.OrderAuditParam;
import com.zemcho.ddql.controller.order.param.OrderRefundParam;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ExchangeOrderService {
    /**
     * 兑换订单列表
     *
     * @param param
     * @return
     */
    Result orderLists(SearchParam param);

    /**
     * 兑换订单统计信息
     *
     * @param param
     * @return
     */
    Result orderStat(SearchParam param);

    /**
     * 兑换订单数据导出
     *
     * @param param
     * @param response
     */
    void orderExport(SearchParam param, HttpServletResponse response);

    /**
     * 兑换订单详情
     *
     * @param param
     * @return
     */
    Result orderInfo(SearchParam param);

    /**
     * 兑换订单-未发货数据导出
     *
     * @param param
     * @param response
     */
    void orderUnDispatchedExport(SearchParam param, HttpServletResponse response);

    /**
     * 兑换订单-导入物流单号
     *
     * @param file
     * @return
     */
    Result importExpressNo(MultipartFile file);

    /**
     * 兑换订单-退货
     *
     * @param param
     * @param token
     * @return
     */
    Result orderRefund(OrderRefundParam param, String token);

    /**
     * 兑换订单-退货申请列表
     *
     * @param param
     * @return
     */
    Result orderRefundApplyLists(SearchParam param);

    /**
     * 兑换订单-退货申请详情
     *
     * @param param
     * @return
     */
    Result orderRefundApplyInfo(SearchParam param);

    /**
     * 兑换订单-退货审核
     *
     * @param param
     * @param token
     * @return
     */
    Result orderRefundAudit(OrderAuditParam param, String token);
}
