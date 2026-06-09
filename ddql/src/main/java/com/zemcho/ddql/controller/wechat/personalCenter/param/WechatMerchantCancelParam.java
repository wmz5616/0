package com.zemcho.ddql.controller.wechat.personalCenter.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 小程序商家注销请求参数
 */
@Data
public class WechatMerchantCancelParam {
    /**
     * 商家ID
     */
    @NotNull(message = "商家ID不能为空")
    private Integer shopId;
}
