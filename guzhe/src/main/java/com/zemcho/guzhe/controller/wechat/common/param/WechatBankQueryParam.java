package com.zemcho.guzhe.controller.wechat.common.param;

import lombok.Data;

/**
 * 小程序银行联行信息查询参数
 */
@Data
public class WechatBankQueryParam {
    /**
     * 银行编码
     */
    private String bank;

    /**
     * 是否过滤空的银行编码数据
     */
    private Boolean isFilterNullBank;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 省份编码
     */
    private String province;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 城市编码
     */
    private String city;

    /**
     * 联行号
     */
    private String bankLinkNo;

    /**
     * 联行名称
     */
    private String bankLinkName;
}
