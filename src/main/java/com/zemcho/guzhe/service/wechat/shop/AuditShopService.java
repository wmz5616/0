package com.zemcho.guzhe.service.wechat.shop;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.shop.param.QualificationCertParam;
import com.zemcho.guzhe.controller.wechat.shop.param.AuditShopParam;

public interface AuditShopService {

    /**
     * 小程序提交商家信息审核
     * @param auditShopParam 参数
     * @param token token
     * @return result
     */
    Result submitAuditShop(AuditShopParam auditShopParam, String token);

    /**
     * 小程序提交资质认证
     * @param qualificationCertParam 参数
     * @param token token
     * @return result
     */
    Result submitQualification(QualificationCertParam qualificationCertParam, String token);

    /**
     * 小程序查询个人申请入驻记录
     * @param param param
     * @param token token
     * @return result
     */
    Result getOwnApplyList(SearchParam param, String token);

    /**
     * 小程序根据id查询申请入驻记录
     *
     * @param param searchId 申请入驻记录ID
     * @param token token
     * @return result
     */
    Result getAuditShopById(String token, SearchParam param);


    Result checkMerchantAdmin(String token);
}
