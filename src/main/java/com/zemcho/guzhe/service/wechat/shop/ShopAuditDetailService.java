package com.zemcho.guzhe.service.wechat.shop;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.wechat.shop.param.ShopUpdateParam;

/**
 * @author HXH
 */
public interface ShopAuditDetailService {
    Result getBusinessData(SearchParam param, String token);

    Result getShopInfo(SearchParam param, String token);

    Result update(ShopUpdateParam param, String token);
}
