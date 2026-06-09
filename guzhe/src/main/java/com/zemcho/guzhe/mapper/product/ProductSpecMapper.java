package com.zemcho.guzhe.mapper.product;

import com.zemcho.guzhe.entity.product.ProductSpec;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品规格类型Mapper
 */
public interface ProductSpecMapper {

    Integer insert(@Param("data") ProductSpec data);

    Integer batchInsert(@Param("list") List<ProductSpec> list);

    Integer deleteByProductId(@Param("productId") Integer productId);

    List<Integer> selectIdsByProductId(@Param("productId") Integer productId);

    Integer update(@Param("spec") ProductSpec spec);
}
