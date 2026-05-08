package com.zemcho.guzhe.mapper.shop;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.entity.shop.ShopAudit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author HXH
 */
@Mapper
public interface ShopAuditMapper {
    int insert(@Param("data") ShopAudit shopAudit);

    ShopAudit selectById(@Param("id") Integer id);

    int update(@Param("data") ShopAudit shopAudit);

    ShopAudit selectByName(@Param("name") String name);

    ShopAudit selectByShopId(@Param("shopId") Integer shopId);
}
