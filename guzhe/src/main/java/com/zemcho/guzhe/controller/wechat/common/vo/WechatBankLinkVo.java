package com.zemcho.guzhe.controller.wechat.common.vo;

import lombok.Data;

/**
 * 小程序银行联行下拉项
 */
@Data
public class WechatBankLinkVo {
    /**
     * 联行号
     */
    private String bankLinkNo;

    /**
     * 联行名称
     */
    private String bankLinkName;
}
