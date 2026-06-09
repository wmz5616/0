package com.zemcho.ddql.service.wechat.shop;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.controller.wechat.shop.param.SettlementRecordSearchParam;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 小程序商家端结算记录服务
 */
public interface WechatShopSettlementRecordService {
    Result lists(SettlementRecordSearchParam param, String token);

    void export(SettlementRecordSearchParam param, String token, HttpServletResponse response);
}
