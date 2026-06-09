package com.zemcho.guzhe.mapper.shop;

import com.zemcho.guzhe.entity.shop.ShopAuditManager;
import com.zemcho.guzhe.entity.shop.ShopManager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author HXH
 */
@Mapper
public interface ShopAuditManagerMapper {
    Integer insert(@Param("data") ShopAuditManager data);

    List<Integer> selectShopAuditIdsByPhone(@Param("phone") String phone);

    List<ShopAuditManager> selectByShopAuditId(@Param("id") Integer id);

    Integer deleteByShopAuditId(@Param("id") Integer id);
}
