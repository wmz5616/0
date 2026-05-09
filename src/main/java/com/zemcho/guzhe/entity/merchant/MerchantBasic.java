package com.zemcho.guzhe.entity.merchant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 基本信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantBasic {
    // 商户名称
    private String merchantName;

    // 联系电话
    private String contactPhone;

    // 邮箱
    private String email;
}
