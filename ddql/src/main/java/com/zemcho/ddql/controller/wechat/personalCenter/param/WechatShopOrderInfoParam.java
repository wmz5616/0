package com.zemcho.ddql.controller.wechat.personalCenter.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WechatShopOrderInfoParam {
    @NotNull(message = "订单ID不能为空")
    private Integer id;
}
