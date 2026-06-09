package com.zemcho.guzhe.entity.merchant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//门店信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchanStore {
    // 门店名称
    private String storeName;
    // 省代码
    private String storeProvince;
    // 市代码
    private String storeCity;
    // 区代码
    private String storeCounty;
    // 详细地址
    private String storeAddr;
}
