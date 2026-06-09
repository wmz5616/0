package com.zemcho.guzhe.mapper.product;

import com.zemcho.guzhe.entity.product.ProductCategoryRelation;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author HXH
 */
public interface ProductCategoryRelationMapper {

    Integer deleteByProductId(@Param("id") Integer id);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertBatch(@Param("data") List<ProductCategoryRelation> data);

    Integer deleteByProductIds(@Param("productIds") List<Integer> productIds);
}
