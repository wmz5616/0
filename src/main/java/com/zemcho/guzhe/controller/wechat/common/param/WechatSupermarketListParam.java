package com.zemcho.guzhe.controller.wechat.common.param;

import lombok.Data;

/**
 * 小程序商超下拉查询参数
 */
@Data
public class WechatSupermarketListParam {
    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;
}
