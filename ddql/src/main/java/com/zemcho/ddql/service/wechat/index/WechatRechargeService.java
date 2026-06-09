package com.zemcho.ddql.service.wechat.index;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.wechat.index.param.RechargeOrderParam;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface WechatRechargeService {
    /**
     * 获取充值活动列表
     *
     * @param param
     * @param token
     * @return
     */
    Result selectActivityList(SearchParam param, String token);

    /**
     * 添加充值订单
     *
     * @param param
     * @param token
     * @return
     */
    Result addOrder(RechargeOrderParam param, String token);

    /**
     * 获取充值订单详情
     *
     * @param param
     * @return
     */
    Result orderInfo(SearchParam param);

    /**
     * 获取充值订单支付配置信息
     *
     * @param param
     * @param token
     * @return
     */
    Result orderPayConfig(SearchParam param, String token);

    /**
     * 充值订单支付回调
     *
     * @param body
     * @param headers
     * @return
     */
    ResponseEntity<String> orderPayCallback(String body, Map<String, String> headers);

    /**
     * 获取充值订单列表
     *
     * @param param
     * @param token
     * @return
     */
    Result orderLists(SearchParam param, String token);

    /**
     * 获取充值订单统计数据
     *
     * @param param
     * @param token
     * @return
     */
    Result orderCountData(SearchParam param, String token);

    /**
     * 充值订单数据导出到邮箱
     *
     * @param param
     * @param token
     * @param request
     * @return
     */
    Result orderExportToEmail(SearchParam param, String token, HttpServletRequest request);
}
