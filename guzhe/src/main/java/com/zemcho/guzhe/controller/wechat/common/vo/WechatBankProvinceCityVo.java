package com.zemcho.guzhe.controller.wechat.common.vo;

import lombok.Data;

/**
 * 小程序银行省市下拉项
 */
@Data
public class WechatBankProvinceCityVo {
    /**
     * 省份编码
     */
    private String province;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 城市编码
     */
    private String city;

    /**
     * 城市名称
     */
    private String cityName;
}
