package com.zemcho.guzhe.controller.merchant.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantSaveParam {

    // 主键ID
    private Integer id;

    // 操作类型：0=个人经营者，1=企业/个体工商户/事业单位
    private String operationType = "";

    // 商户名称
    private String merchantName = "";

    // 联系电话
    private String contactPhone = "";

    // 邮箱
    private String email = "";

    // 身份证名称
    private String cardName = "";

    // 身份证号码
    private String cardNo = "";

    // 手机
    private String cardMobile = "";

    // 身份证开始日期
    private String cardBeginDate = "";

    // 身份证结束日期(可填"长期")
    private String cardEndDate = "";

    // 证件类型：10060="居民身份证"，10061="护照"，10062="港澳通行证"，10063="台胞证"
    private Integer credentialsType = 0;

    // 门店名称
    private String storeName = "";

    // 省代码
    private String storeProvince = "";

    // 市代码
    private String storeCity = "";

    // 区代码
    private String storeCounty = "";

    // 详细地址
    private String storeAddr = "";

    // 营业执照名称
    private String businessName = "";

    // 营业执照编号
    private String businessNo = "";

    // 主体类型：10030=企业商户，10031=个体工商户，10034=民办非企业，10033=事业单位
    private Integer mainType = 0;

    // 法人名称
    private String legalPerson = "";

    // 营业地址省代码
    private String businessProvince = "";

    // 营业地址市代码
    private String businessCity = "";

    // 营业地址区代码
    private String businessCounty = "";

    // 营业详细地址
    private String businessAddr = "";

    // 营业执照开始日期
    private String businessBeginDate = "";

    // 营业执照截止日期(可填"长期")
    private String businessEndDate = "";

    // 账户类型（操作类型为1必填）：10070=对公账户，10071=法人账户
    private Integer accType = 0;

    // 银行账号
    private String accCardNo = "";

    // 银行开户预留手机号
    private String accMobile = "";

    // 户名（法人或营业执照名称）
    private String accName = "";

    // 开户行编号
    private String bank = "";

    // 开户行名称
    private String bankName = "";

    // 支行联行号
    private String bankLinkNo = "";

    // 支行名称
    private String bankBranch = "";

    // 开户省代码
    private String province = "";

    // 开户市代码
    private String city = "";

    // 主商户编号
    private String parentChannelMerchantNo = "";

    // 法人证件正面照片地址
    private String legalLicenceFrontUrl = "";

    // 法人证件正面照片地址(本地)
    private String legalLicenceFrontUrlLocal = "";

    // 法人证件背面照片地址
    private String legalLicenceBackUrl = "";

    // 法人证件背面照片地址(本地）
    private String legalLicenceBackUrlLocal = "";

    // 营业执照照片地址（操作类型为1时必填）
    private String licenceUrl = "";

    // 营业执照照片地址(本地)（操作类型为1时必填）
    private String licenceUrlLocal = "";

    // 开户许可证/法人银行卡照片地址（操作类型为1时必填）
    private String openAccountLicenceUrl = "";

    // 开户许可证/法人银行卡照片地址(本地)
    private String openAccountLicenceUrlLocal = "";

    // 结算类型
    private String settleType = "";

    // 登录账号
    private String loginAccount = "";

    // 商户编号
    private String merchantNo = "";

    // 入网请求号（查询入网进度用）
    private String requestNo = "";

    // 申请单编号
    private String applicationNo = "";

    // 申请状态
    private String applicationStatus = "";

    // “申请已驳回”或者“申请已完成”时，回传的审核意见
    private String auditOpinion = "";

    // 入驻商户电签地址
    private String signUrl = "";

    // 状态 0禁用 1启用 2审核中 3审核被驳回 4保存
    private Integer status;

    // 小程序端创建用户id
    private Integer userId = 0;

    // 小程序端创建入口商家id
    private Integer shopId;
}