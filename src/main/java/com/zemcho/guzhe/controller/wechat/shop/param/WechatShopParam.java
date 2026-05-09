package com.zemcho.guzhe.controller.wechat.shop.param;


import com.zemcho.guzhe.common.param.PageParam;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WechatShopParam extends PageParam {

    @NotNull(message = "位置不能为空")
    private String location;
}
