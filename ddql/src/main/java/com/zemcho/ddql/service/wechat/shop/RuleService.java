package com.zemcho.ddql.service.wechat.shop;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.param.ShopManagerParam;

/**
 * @author HXH
 */
public interface RuleService {
    Result updateShopStatus(SearchParam param, String token);

    Result selectByPhone(SearchParam param, String token);

    Result update(SearchParam param, String token);
}
