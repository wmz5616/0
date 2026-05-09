package com.zemcho.guzhe.mapper.shop;

import com.zemcho.guzhe.entity.shop.ShopPoster;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface ShopPosterMapper {
    /**
     * 批量插入
     *
     * @param data
     * @return
     */
    Integer insertAll(@Param("data") Collection<ShopPoster> data);

    /**
     * 根据商家ID删除
     *
     * @param shopId
     * @return
     */
    Integer deleteByShopId(@Param("shopId") Integer shopId);

    /**
     * 根据商家ID查询
     *
     * @param shopId
     * @return
     */
    List<ShopPoster> selectByShopId(@Param("shopId") Integer shopId);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    ShopPoster selectById(@Param("id") Integer id);

    /**
     * 根据商家ID查询
     *
     * @param shopIds
     * @return
     */
    List<ShopPoster> selectByShopIds(@Param("shopIds") Collection<Integer> shopIds);
}
