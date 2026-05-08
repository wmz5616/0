package com.zemcho.guzhe.service.shop;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.wechat.shop.param.ShopAuditHandleParam;

/**
 * @author HXH
 */
public interface ShopAuditService {
    Result selectList(SearchParam param);

    Result detail(SearchParam param);

    Result handle(SearchParam param, String token, ShopAuditHandleParam shopAuditHandleParam);

    Result get(SearchParam param, String token);

    Result auditQualificationCert(SearchParam param, String token);
}
