package com.zemcho.guzhe.util.tgy.dto;

import com.zemcho.guzhe.entity.merchant.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 商户进件请求用的Json实体
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantRequestJson {
    // 账户信息
    private Acc acc;

    //private Integer feeType;

    // 法人证件背面照片地址
    private String legalLicenceBackUrl;

    // 法人证件正面照片地址
    private String legalLicenceFrontUrl;

    // 门店信息
    private MerchanStore merchanStore;

    // 商户基础信息
    private MerchantBasic merchantBasic;

    // 商户证件信息
    private MerchantCard merchantCard;

    // 营业执照照片地址（操作类型为1时必填）
    private String licenceUrl;

    // 开户许可证/法人银行卡照片地址（操作类型为1时必填）
    private String openAccountLicenceUrl;

    // 商户营业信息
    private MerchanBusiness merchanBusiness;

    // 操作类型 1: 新增商户 2: 修改商户
    private String operationType;

    // 主商户编号
    private String parentChannelMerchantNo;

    //private Integer rate;

    // 结算类型
    private String settleType;

    // 登录账号
    private String loginAccount;

    private String sign;


    // 转Merchant 实体为MerchantRequestJson
    public static MerchantRequestJson MerchantToMerchantRequestJson(Merchant merchant) {
        MerchantRequestJson merchantRequestJson = new MerchantRequestJson();
        merchantRequestJson.setAcc(new Acc(merchant.getAccType(), merchant.getAccCardNo(), merchant.getAccMobile(), merchant.getAccName(), merchant.getBank(), merchant.getBankName(), merchant.getBankLinkNo(), merchant.getBankBranch(), merchant.getProvince(), merchant.getCity()));
        merchantRequestJson.setMerchanStore(new MerchanStore(merchant.getStoreName(), merchant.getStoreProvince(), merchant.getStoreCity(), merchant.getStoreCounty(), merchant.getStoreAddr()));
        merchantRequestJson.setMerchantBasic(new MerchantBasic(merchant.getMerchantName(), merchant.getContactPhone(), merchant.getEmail()));
        merchantRequestJson.setMerchantCard(new MerchantCard(merchant.getCardName(), merchant.getCardNo(), merchant.getCardMobile(), merchant.getCardBeginDate(), merchant.getCardEndDate(), merchant.getCredentialsType()));
        merchantRequestJson.setMerchanBusiness(new MerchanBusiness(merchant.getBusinessName(), merchant.getBusinessNo(), merchant.getMainType(), merchant.getLegalPerson(), merchant.getBusinessProvince(), merchant.getBusinessCity(), merchant.getBusinessCounty(), merchant.getBusinessAddr(), merchant.getBusinessBeginDate(), merchant.getBusinessEndDate()));
        merchantRequestJson.setOperationType(merchant.getOperationType());
        merchantRequestJson.setParentChannelMerchantNo(merchant.getParentChannelMerchantNo());
        merchantRequestJson.setSettleType(merchant.getSettleType());
        merchantRequestJson.setLoginAccount(merchant.getLoginAccount());
        merchantRequestJson.setLegalLicenceBackUrl(merchant.getLegalLicenceBackUrl());
        merchantRequestJson.setLegalLicenceFrontUrl(merchant.getLegalLicenceFrontUrl());
        merchantRequestJson.setLicenceUrl(merchant.getLicenceUrl());
        merchantRequestJson.setOpenAccountLicenceUrl(merchant.getOpenAccountLicenceUrl());
        merchantRequestJson.setSign(merchant.getSignUrl());

        return merchantRequestJson;
    }

}
