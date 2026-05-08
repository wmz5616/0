package com.zemcho.guzhe.entity.merchant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 身份证信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantCard {
    // 身份证名称
    private String cardName;
    // 身份证号码
    private String cardNo;
    // 手机
    private String cardMobile;
    // 身份证开始日期
    private String cardBeginDate;
    // 身份证结束日期(可填"长期")
    private String cardEndDate;
    // 10060:"居民身份证"，10061:"护照"，10062:"港澳通行证"，10063:"台胞证"
    private Integer credentialsType;
}
