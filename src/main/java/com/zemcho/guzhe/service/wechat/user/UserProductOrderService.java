package com.zemcho.guzhe.service.wechat.user;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.wechat.user.param.ProductOrderAddParam;
import com.zemcho.guzhe.controller.wechat.user.param.ProductOrderRefundRefundParam;
import com.zemcho.guzhe.controller.wechat.user.param.ProductOrderUpdateBaseParam;
import com.zemcho.guzhe.util.tgy.dto.WxJsPayCallBackResponse;

public interface UserProductOrderService {
    /**
     * 添加商品订单
     *
     * @param param
     * @param token
     * @return
     */
    Result addProductOrder(ProductOrderAddParam param, String token);

    /**
     * 获取商品订单详情
     *
     * @param param
     * @return
     */
    Result userProductOrderInfo(SearchParam param);

    /**
     * 更新商品订单基础信息
     *
     * @param param
     * @param token
     * @return
     */
    Result updateProductOrderBase(ProductOrderUpdateBaseParam param, String token);

    /**
     * 获取商品订单支付配置信息
     *
     * @param param
     * @param token
     * @return
     */
    Result productOrderPayConfig(SearchParam param, String token);

    /**
     * 商品订单支付回调
     *
     * @param response
     * @return
     */
    String productOrderPayCallBack(WxJsPayCallBackResponse response);

    /**
     * 获取用户商品订单列表
     *
     * @param param
     * @param token
     * @return
     */
    Result userProductOrderLists(SearchParam param, String token);

    /**
     * 获取用户商品订单统计信息
     *
     * @param param
     * @param token
     * @return
     */
    Result userProductOrderStat(SearchParam param, String token);

    /**
     * 商品订单退款申请
     *
     * @param param
     * @param token
     * @return
     */
    Result productOrderRefund(ProductOrderRefundRefundParam param, String token);
}
