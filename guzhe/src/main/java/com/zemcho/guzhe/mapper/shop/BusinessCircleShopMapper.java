package com.zemcho.guzhe.mapper.shop;

import com.zemcho.guzhe.entity.shop.BusinessCircleShop;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface BusinessCircleShopMapper {

    /**
     * 插入商圈店铺关联关系
     *
     * @param data 商圈店铺关联实体对象
     * @return 插入记录数
     */
    int insert(@Param("data") BusinessCircleShop data);

    /**
     * 批量插入商圈店铺关联关系
     *
     * @param dataList 商圈店铺关联实体对象列表
     * @return 插入记录数
     */
    int insertBatch(@Param("dataList") List<BusinessCircleShop> dataList);

    /**
     * 根据ID更新商圈店铺关联关系
     *
     * @param data 商圈店铺关联实体对象
     * @return 更新记录数
     */
    int update(@Param("data") BusinessCircleShop data);

    /**
     * 根据ID查询商圈店铺关联关系
     *
     * @param id 关联关系ID
     * @return 商圈店铺关联实体对象
     */
    BusinessCircleShop selectById(@Param("id") Integer id);

    /**
     * 根据商圈ID查询关联的店铺ID列表
     *
     * @param circleId 商圈ID
     * @return 店铺ID列表
     */
    List<Integer> selectShopIdsByCircleId(@Param("circleId") Integer circleId);

    /**
     * 根据店铺ID查询关联的商圈ID列表
     *
     * @param shopId 店铺ID
     * @return 商圈ID列表
     */
    List<Integer> selectCircleIdsByShopId(@Param("shopId") Integer shopId);

    /**
     * 根据商圈ID删除关联关系
     *
     * @param circleId 商圈ID
     * @return 删除记录数
     */
    int deleteByCircleId(@Param("circleId") Integer circleId);

    /**
     * 根据店铺IDs删除关联关系
     *
     * @param shopIds 店铺IDs
     * @return 删除记录数
     */
    int deleteByShopIds(@Param("shopIds") Collection<Integer> shopIds);

    /**
     * 根据商圈ID和店铺ID删除关联关系
     *
     * @param circleId 商圈ID
     * @param shopId   店铺ID
     * @return 删除记录数
     */
    int deleteByCircleIdAndShopId(@Param("circleId") Integer circleId, @Param("shopId") Integer shopId);

    /**
     * 批量删除商圈店铺关联关系
     *
     * @param ids 关联关系ID列表
     * @return 删除记录数
     */
    int deleteByIds(@Param("ids") List<Integer> ids);

    /**
     * 根据商圈ID查询关联关系
     */
    Boolean existShop(@Param("circleIds") Collection<Integer> circleIds);

    String select(@Param("circleId") Integer circleId);
}
