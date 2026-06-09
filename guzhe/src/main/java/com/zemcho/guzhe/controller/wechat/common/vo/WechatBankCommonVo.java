package com.zemcho.guzhe.controller.wechat.common.vo;

import lombok.Data;

/**
 * 小程序银行下拉项
 */
@Data
public class WechatBankCommonVo {
    /**
     * 银行编码
     */
    private String bank;

    /**
     * 银行名称
     */
    private String bankName;
}
