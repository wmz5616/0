package com.zemcho.guzhe.service.shop;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.shop.param.QualificationCertParam;

public interface QualificationCertService {
    /**
     * 新增资质认证
     *
     * @param param 参数对象
     * @param token token
     * @return 结果
     */
    Result addQualificationCert(QualificationCertParam param, String token);
    
    /**
     * 更新资质认证
     * @param param 参数对象
     * @return 结果
     */
    Result updateQualificationCert(QualificationCertParam param);


    
    /**
     * 根据商家ID查询资质认证
     * @param param 查询参数(包含shopId)
     * @return 结果
     */
    Result getByShopId(SearchParam param);
    
    /**
     * 审核资质认证
     *
     * @param param         keyword 驳回原因 searchId 资质认证id searchType 是否通过审核
     * @param token        审核人token
     * @return 结果
     */
    public Result auditQualificationCert(SearchParam param, String token);
}