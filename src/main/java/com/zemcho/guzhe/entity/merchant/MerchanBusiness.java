package com.zemcho.guzhe.entity.merchant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//营业信息（操作类型为1时必填）
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchanBusiness {

    // 营业执照名称
    private String businessName;

    // 营业执照编号
    private String businessNo;

    // 主体类型：10030=企业商户，10031=个体工商户，10034=民办非企业，10033=事业单位
    private Integer mainType;

    // 法人名称
    private String legalPerson;

    // 营业地址省代码
    private String businessProvince;

    // 营业地址市代码
    private String businessCity;

    // 营业地址区代码
    private String businessCounty;

    // 营业详细地址
    private String businessAddr;

    // 营业执照开始日期
    private String businessBeginDate;

    // 营业执照结束日期
    private String businessEndDate;

}
