package com.zemcho.guzhe.mapper.product;

import com.zemcho.guzhe.entity.product.ProductSpecPrice;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品规格价格库存Mapper
 */
public interface ProductSpecPriceMapper {

    Integer insert(@Param("data") ProductSpecPrice data);


    Integer batchInsert(@Param("list") List<ProductSpecPrice> list);

    Integer deleteByProductId(@Param("productId") Integer productId);

    Integer sumStockByProductId(@Param("productId") Integer productId);

    List<ProductSpecPrice> selectByProductId(@Param("productId") Integer productId);
}