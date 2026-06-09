package com.zemcho.ddql.controller.wechat.order.param;

import com.zemcho.ddql.controller.wechat.personalCenter.param.WechatShopOrderListParam;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author HXH
 */
@Data
public class WechatShopOrderParam extends WechatShopOrderListParam {
    /**
     * 商家ID
     */
    @NotNull(message = "商家id不能为空")
    private Integer id;

    private String orderNo;

    private String nickName;
}
