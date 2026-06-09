package com.zemcho.guzhe.mapper.shop;

import com.zemcho.guzhe.entity.shop.ShopAuditIndustry;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author HXH
 */
@Mapper
public interface ShopAuditIndustryMapper {
    int insertBatch(@Param("list") List<ShopAuditIndustry> list);

    List<ShopAuditIndustry> selectByShopAuditId(@Param("shopAuditId") Integer shopAuditId);

    int deleteByShopAuditId(@Param("shopAuditId") Integer shopAuditId);
}
