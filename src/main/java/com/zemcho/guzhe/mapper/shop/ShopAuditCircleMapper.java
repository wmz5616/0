package com.zemcho.guzhe.mapper.shop;

import com.zemcho.guzhe.entity.shop.ShopAuditCircle;
import com.zemcho.guzhe.entity.shop.ShopAuditIndustry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author HXH
 */
@Mapper
public interface ShopAuditCircleMapper {
    int insertBatch(@Param("list") List<ShopAuditCircle> list);

    List<ShopAuditCircle> selectByShopAuditId(@Param("shopAuditId") Integer shopAuditId);

    int deleteByShopAuditId(@Param("shopAuditId") Integer shopAuditId);
}
