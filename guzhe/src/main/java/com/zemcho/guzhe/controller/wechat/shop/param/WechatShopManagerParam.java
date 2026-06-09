package com.zemcho.guzhe.controller.wechat.shop.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @title: WechatShopManagerParam
 * @Description:
 * @Date: 2026/5/7 17:17
 */
@Data
public class WechatShopManagerParam {
    @NotNull(message = "商家ID不能为空")
    private Integer shopId;

    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @NotNull(message = "管理者类型不能为空")
    private Integer headManager;
}
