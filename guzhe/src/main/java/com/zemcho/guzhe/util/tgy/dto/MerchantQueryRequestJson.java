package com.zemcho.guzhe.util.tgy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantQueryRequestJson {

    private String requestNo;

    private String loginAccount;

    private String sign;
}
