package com.zemcho.ddql.service.wechat.shop;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.audit.param.ShopAuditHandleParam;
import com.zemcho.ddql.controller.business.param.BusinessCircleParam;
import com.zemcho.ddql.controller.business.param.QualificationCertParam;
import com.zemcho.ddql.controller.wechat.shop.param.AuditShopParam;

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
     * 后台获取商家信息审核列表
     * @param param 参数
     * @param token token
     * @return result
     */
    Result getAuditShopList(SearchParam param, String token);

    /**
     * 处理审核信息
     * @param param searchId 申请信息id keyword 驳回元婴 searchType 是否通过
     * @param token token
     * @return result
     */
    Result handleAuditShop(ShopAuditHandleParam param, String token);

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

    /**
     * 后台查询还未通过审核的商家
     * @param param searchId 申请信息id
     * @param token token
     * @return result
     */
    Result getUnAuditShop(SearchParam param, String token);

    Result checkMerchantAdmin(String token);

    Result getQualification(SearchParam param, String token);

}
