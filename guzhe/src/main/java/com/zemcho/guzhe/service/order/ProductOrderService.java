package com.zemcho.guzhe.service.order;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.order.param.OrderAuditParam;
import com.zemcho.guzhe.controller.order.param.OrderRefundParam;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProductOrderService {
    /**
     * 获取商品订单列表
     *
     * @param param
     * @return
     */
    Result productOrderLists(SearchParam param, String token, Boolean isWechat);

    /**
     * 获取商品订单统计信息
     *
     * @param param
     * @return
     */
    Result productOrderStat(SearchParam param, String token, Boolean isWechat);

    /**
     * 导出商品订单数据
     *
     * @param param
     * @param response
     */
    void productOrderExport(SearchParam param, HttpServletResponse response, String token, Boolean isWechat);

    /**
     * 获取商品订单详情
     *
     * @param param
     * @return
     */
    Result productOrderInfo(SearchParam param, String token, Boolean isWechat);

    /**
     * 商品订单--未发货数据导出
     *
     * @param param
     * @param response
     */
    void productOrderUnDispatchedExport(SearchParam param, HttpServletResponse response, String token, Boolean isWechat);

    /**
     * 商品订单--导入物流单号
     *
     * @param file
     * @return
     */
    Result importExpressNo(MultipartFile file);

    /**
     * 商品订单--退款
     *
     * @param param
     * @param token
     * @return
     */
    Result orderRefund(OrderRefundParam param, String token, Boolean isWechat);

    /**
     * 获取商品订单退款申请列表
     *
     * @param param
     * @return
     */
    Result orderRefundApplyLists(SearchParam param, String token, Boolean isWechat);

    /**
     * 获取商品订单退款申请详情
     *
     * @param param
     * @return
     */
    Result orderRefundApplyInfo(SearchParam param);

    /**
     * 商品订单--退款审核
     *
     * @param param
     * @param token
     * @return
     */
    Result orderRefundAudit(OrderAuditParam param, String token, Boolean isWechat);

    /**
     * 商品订单--券码核销
     *
     * @param param
     * @param token
     * @return
     */
    Result productOrderTicketCheck(SearchParam param, String token);
}
