package com.zemcho.guzhe.mapper.product;

import com.zemcho.guzhe.entity.product.ProductSpecValue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品规格值Mapper
 */
public interface ProductSpecValueMapper {

    Integer insert(@Param("data") ProductSpecValue data);

    Integer batchInsert(@Param("list") List<ProductSpecValue> list);

    Integer deleteByTypeIds(@Param("typeIds") List<Integer> typeIds);

    Integer update(@Param("value") ProductSpecValue value);

    List<ProductSpecValue> selectByTypeId(@Param("typeId") Integer typeId);

    ProductSpecValue selectByTypeIdAndValueName(@Param("typeId") Integer typeId, @Param("valueName") String valueName);
}
