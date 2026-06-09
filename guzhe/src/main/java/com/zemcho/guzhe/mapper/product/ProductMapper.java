package com.zemcho.guzhe.mapper.product;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.app.vo.AppProductListVo;
import com.zemcho.guzhe.controller.product.param.ProductSearchParam;
import com.zemcho.guzhe.entity.product.Product;
import com.zemcho.guzhe.entity.product.ProductCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author HXH
 */
public interface ProductMapper {
    /**
     * 新增商品
     *
     * @param data 商品
     */
    Integer insert(@Param("data") Product data);

    Product selectById(@Param("id") Integer id);

    Integer update(@Param("data") Product data);

    List<Product> selectList(@Param("param") ProductSearchParam param);

    Integer deleteByIds(@Param("productIds") Set<Integer> productIds);

    Integer inc(@Param("id") Integer id, @Param("stock") Integer stock, @Param("exchangeNum") Integer exchangeNum);

    Boolean ifExists(@Param("categoryId") Integer categoryId);

    /**
     * 商品库存量/销量更新--正数为自增、负数为自减
     *
     * @param id
     * @param stock
     * @param saleNum
     * @return
     */
    Integer updateStockOrSaleNum(@Param("id") Integer id, @Param("stock") Integer stock,
                                 @Param("saleNum") Integer saleNum);

    List<Product> selectByCategoryId(@Param("param") SearchParam param);

    Boolean ifExistsProduct(@Param("deleteId") Integer deleteId);

    Integer updateStatusByIds(@Param("ids") List<Integer> ids, @Param("status") Integer status);

    /**
     * app端--查询商品列表
     *
     * @param param
     * @return
     */
    List<AppProductListVo> selectListByApp(@Param("param") SearchParam param);
}
