package com.zemcho.ddql.mapper.product;

import com.zemcho.ddql.controller.product.param.ProductSearchParam;
import com.zemcho.ddql.entity.product.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

public interface ProductMapper {
    /**
     * 批量插入商品
     *
     * @param data
     * @return
     */
    Integer insert(@Param("data") Product data);

    /**
     * 获取商品列表
     *
     * @param param
     * @return
     */
    List<Product> selectList(@Param("param") ProductSearchParam param);

    /**
     * 批量更新商品
     *
     * @param product
     * @return
     */
    Integer update(@Param("data") Product product);

    /**
     * 判断商品是否绑定了商品分类
     */
    Boolean ifExists(@Param("categoryId") Integer categoryId);

    /**
     * 根据id查询商品
     *
     * @param productId
     * @return
     */
    Product selectById(@Param("productId") Integer productId);

    /**
     * 删除商品
     *
     * @param productId
     * @return
     */
    @Select("delete from product where id = #{productId}")
    Integer deleteById(Integer productId);

    /**
     * 批量删除商品
     *
     * @param productIds
     * @return
     */
    Integer deleteByIds(@Param("productIds") Set<Integer> productIds);

    /**
     * 商品库存量/兑换数自增
     *
     * @param id
     * @param stock
     * @param exchangeNum
     * @return
     */
    Integer inc(@Param("id") Integer id, @Param("stock") Integer stock, @Param("exchangeNum") Integer exchangeNum);

    /**
     * 商品库存量/兑换数自减
     *
     * @param id
     * @param stock
     * @param exchangeNum
     * @return
     */
    Integer dec(@Param("id") Integer id, @Param("stock") Integer stock, @Param("exchangeNum") Integer exchangeNum);

    /**
     * 商品库存量/兑换数更新--正数为自增、负数为自减
     *
     * @param id
     * @param stock
     * @param exchangeNum
     * @return
     */
    Integer updateStockOrExchangeNum(@Param("id") Integer id, @Param("stock") Integer stock,
                                     @Param("exchangeNum") Integer exchangeNum);


    /**
     * 检查单个商品库存是否充足
     * @param productId 商品ID
     * @param quantity 要购买的数量
     * @return 库存数量（如果库存充足返回库存数，否则返回null）
     */
    Integer checkStock(@Param("productId") Integer productId, @Param("quantity") Integer quantity);

    /**
     * 扣减库存（乐观锁方式）
     * @param productId 商品ID
     * @param quantity 要扣减的数量
     * @return 影响的行数（1表示成功，0表示失败）
     */
    int decreaseStock(@Param("productId") Integer productId, @Param("quantity") Integer quantity);

    /**
     * 批量查询商品库存
     * @param productIds 商品ID列表
     * @return 商品列表（包含库存信息）
     */
    List<Product> batchCheckStock(@Param("productIds") List<Integer> productIds);

    /**
    * 恢复库存（用于退款）
    * @param productId 商品ID
    * @param quantity 要恢复的数量
    * @return 影响的行数
 */
    int increaseStock(@Param("productId") Integer productId, @Param("quantity") Integer quantity);
}
