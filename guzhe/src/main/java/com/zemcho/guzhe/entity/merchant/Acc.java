package com.zemcho.guzhe.entity.merchant;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//账户信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Acc {
    // 账户类型（操作类型为1必填）：10070=对公账户，10071=法人账户
    private Integer accType;

    // 银行账号
    private String accCardNo;

    // 银行账号手机号
    private String accMobile;

    // 户名（法人或营业执照名称）
    private String accName;

    // 开户行编号
    private String bank;

    // 开户行名称
    private String bankName;

    // 支行联行号
    private String bankLinkNo;

    // 支行名称
    private String bankBranch;

    // 开户省代码
    private String province;

    // 开户市代码
    private String city;

}
