package com.zemcho.ddql.controller.wechat.shop.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 微信小程序商家详情请求参数
 */
@Data
public class WechatShopDetailParam {

    /**
     * 商家ID
     */
    @NotNull(message = "商家ID不能为空")
    private Integer shopId;
}
