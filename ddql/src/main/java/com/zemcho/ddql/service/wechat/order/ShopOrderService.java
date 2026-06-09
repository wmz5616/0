package com.zemcho.ddql.service.wechat.order;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.wechat.order.param.ShopOrderRefundParam;
import com.zemcho.ddql.controller.wechat.order.param.WechatShopOrderParam;
import com.zemcho.ddql.controller.wechat.shop.param.ShopOrderCreateParam;
import com.zemcho.ddql.controller.wechat.shop.param.ShopOrderCreateVo;
import com.zemcho.ddql.util.tgy.dto.WxJsPayCallBackResponse;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author HXH
 */
public interface ShopOrderService {
    Result select(WechatShopOrderParam param, String token);

    void businessDataExport(WechatShopOrderParam param, HttpServletResponse response, String token);

    Result selectDetail(SearchParam param, String token);

    Result refund(ShopOrderRefundParam param, String token);

    Result createOrder(ShopOrderCreateParam param, String token);

    Result pay(SearchParam param, String token);

    String shopOrderPayCallBack(WxJsPayCallBackResponse response);
}
