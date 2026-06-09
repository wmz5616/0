package com.zemcho.ddql.mapper.product;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.product.ProductCategory;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

public interface ProductCategoryMapper {
    /**
     * 批量插入商品分类关联
     *
     * @param data
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertBatch(@Param("data") List<ProductCategory> data);

    /**
     * 获取商品分类关联列表
     * @param param
     * @return
     */
    List<ProductCategory> selectList(@Param("param") SearchParam param);

    /**
     * 根据商品id删除商品分类关联
     * @param productId
     * @return
     */
    Integer deleteByProductId(@Param("productId") Integer productId);
    
    /**
     * 根据商品ID查询分类列表
     * @param productId
     * @return
     */
    List<ProductCategory> selectByProductId(@Param("productId") Integer productId);
    
    /**
     * 根据分类ID查询商品列表
     * @param categoryId
     * @return
     */
    List<ProductCategory> selectByCategoryId(@Param("categoryId") Integer categoryId);

    /**
     * 根据商品ID批量删除商品分类关联
     * @param productIds
     * @return
     */
    Integer deleteByProductIds(@Param("productIds") Set<Integer> productIds);
}
