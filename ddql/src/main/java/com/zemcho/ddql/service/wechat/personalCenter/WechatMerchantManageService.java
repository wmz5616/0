package com.zemcho.ddql.service.wechat.personalCenter;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.controller.wechat.personalCenter.param.WechatMerchantCancelParam;
import com.zemcho.ddql.controller.wechat.personalCenter.param.WechatMerchantManageListParam;

/**
 * 小程序商家管理服务
 */
public interface WechatMerchantManageService {
    /**
     * 查询当前用户管理的商家列表
     */
    Result list(WechatMerchantManageListParam param, String token);

    /**
     * 注销商家
     */
    Result cancel(WechatMerchantCancelParam param, String token);
}
