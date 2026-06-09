package com.zemcho.ddql.mapper.product;

import com.zemcho.ddql.entity.product.ProductCheckAdmin;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface ProductCheckAdminMapper {
    /**
     * 批量插入
     *
     * @param data
     * @return
     */
    Integer insertAll(@Param("data") Collection<ProductCheckAdmin> data);

    /**
     * 根据商品ID批量删除
     *
     * @param productIds
     * @return
     */
    Integer deleteByProductIds(@Param("productIds") Collection<Integer> productIds);

    /**
     * 根据管理员ID查询商品ID
     *
     * @param adminId
     * @return
     */
    List<Integer> selectProductIdByAdminId(@Param("adminId") Integer adminId);

    /**
     * 根据商品ID查询管理员ID
     *
     * @param productId
     * @return
     */
    List<Integer> selectAdminIdByProductId(@Param("productId") Integer productId);
}
