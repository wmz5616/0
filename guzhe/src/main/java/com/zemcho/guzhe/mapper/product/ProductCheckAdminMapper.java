package com.zemcho.guzhe.mapper.product;

import com.zemcho.guzhe.entity.product.ProductCheckAdmin;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author HXH
 */
public interface ProductCheckAdminMapper {

    Integer deleteByProductIds(@Param("ids") List<Integer> ids);

    Integer insertAll(@Param("data") List<ProductCheckAdmin> data);

    List<Integer> selectAdminIdByProductId(@Param("productId") Integer productId);

    /**
     * 根据管理员ID查询商品ID
     *
     * @param adminId
     * @return
     */
    List<Integer> selectProductIdByAdminId(@Param("adminId") Integer adminId);
}
