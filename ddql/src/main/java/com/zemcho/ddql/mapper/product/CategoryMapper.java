package com.zemcho.ddql.mapper.product;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.product.vo.CategoryVo;
import com.zemcho.ddql.entity.product.Category;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CategoryMapper {
    /**
     * 批量插入商品分类
     *
     * @param data
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertBatch(@Param("data") List<Category> data);


    /**
     * 获取商品分类列表
     * @param param
     * @return
     */
    List<Category> selectList(@Param("param") SearchParam param);

    /**
     * 批量更新商品分类
     * @param toUpdate
     * @return
     */
    Integer updateBatch(@Param("data") List<Category> toUpdate);

    /**
     * 判断商品分类是否存在
     */
    @Select("select count(1) from category where id = #{id}")
    Boolean ifExists(@Param("id") Integer id);

    /**
     * 根据id查询商品分类
     * @param categoryId
     * @return
     */
    Category selectById(@Param("categoryId") Integer categoryId);

    /**
     * 删除商品分类
     * @param categoryId
     * @return
     */
    @Select("delete from category where id = #{categoryId}")
    Integer deleteById(@Param("categoryId") Integer categoryId);

    /**
     * 根据商品id列表查询商品分类列表
     * @param productIds
     * @return
     */
    List<CategoryVo> selectByProductIds(@Param("productIds") List<Integer> productIds);

    /**
     * 删除商品分类后，更新排序
     * @param deletedSort
     * @return
     */
    Integer updateSortAfterDelete(@Param("deletedSort") Integer deletedSort);
}
