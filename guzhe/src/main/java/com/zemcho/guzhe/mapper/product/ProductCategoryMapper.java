package com.zemcho.guzhe.mapper.product;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.product.vo.CategoryVo;
import com.zemcho.guzhe.entity.product.ProductCategory;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author HXH
 */
public interface ProductCategoryMapper {
    /**
     * 判断商品分类是否存在
     *
     * @param categoryId 商品分类ID
     * @return true:存在 false:不存在
     */
    @Select("select count(1) from product_category where id = #{categoryId} and shop_id = #{shopId}")
    Boolean ifExists(@Param("categoryId") Integer categoryId,@Param("shopId") Integer shopId);

    List<CategoryVo> selectByProductIds(@Param("productIds") List<Integer> productIds);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertBatch(@Param("data") List<ProductCategory> data);

    Integer updateBatch(@Param("data")List<ProductCategory> data);

    List<ProductCategory> selectList(@Param("param") SearchParam param);

    ProductCategory selectById(@Param("categoryId") Integer categoryId);

    @Select("delete from product_category where id = #{categoryId}")
    Integer deleteById(@Param("categoryId") Integer categoryId);

    Integer updateSortAfterDelete(@Param("deletedSort") Integer deletedSort);
}
