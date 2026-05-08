package com.zemcho.guzhe.util.tgy.dto;

import lombok.Data;

@Data
public class WxJsRefundJson {
    private String account;
    // 通莞订单号
    private String upOrderId;
    // 退款金额单位元
    private Double refundMoney;
    // 分账订单退款时，可以指定从分账方退还分账金额（乐刷不需要）
    private String accountDivided;
    // FUND_ACCOUNT：资金账户余额，不传默认为未结算资金退款
    private String refundAccountType;
    // 签名
    private String sign;

}
