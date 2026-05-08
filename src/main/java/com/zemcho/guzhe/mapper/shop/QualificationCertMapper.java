package com.zemcho.guzhe.mapper.shop;

import com.zemcho.guzhe.controller.shop.vo.QualificationCertVO;
import com.zemcho.guzhe.entity.shop.QualificationCert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QualificationCertMapper {
    
    /**
     * 插入资质认证信息
     * @param data 资质认证实体对象
     * @return 插入记录数
     */
    int insert(@Param("data") QualificationCert data);
    
    /**
     * 更新资质认证信息
     * @param data 资质认证实体对象
     * @return 更新记录数
     */
    int update(@Param("data") QualificationCert data);
    
    /**
     * 根据ID查询资质认证信息
     * @param id 资质认证ID
     * @return 资质认证实体对象
     */
    QualificationCert selectById(@Param("id") Integer id);
    
    /**
     * 根据商家ID查询资质认证信息
     * @param shopId 商家ID
     * @return 资质认证实体对象
     */
    QualificationCert selectByShopId(@Param("shopId") Integer shopId);

    /**
     * 根据商家ID查询未审核或通过的资质认证信息
     * @param shopId 商家ID
     * @return 资质认证实体对象
     */
    QualificationCert selectExistByShopId(@Param("shopId") Integer shopId);


    QualificationCert selectExistByUserId(@Param("userId") Integer userId);

    QualificationCertVO selectByShopAuditId(@Param("searchId") Integer searchId);
}