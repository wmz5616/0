package com.zemcho.ddql.controller.wechat.shop.param;

import com.zemcho.ddql.common.param.PageParam;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WechatShopParam extends PageParam {

    @NotNull(message = "位置不能为空")
    private String location;
}
