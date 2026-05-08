package com.zemcho.guzhe.mapper.shop;

import com.zemcho.guzhe.entity.shop.ShopManager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface ShopManagerMapper {
    /**
     * 根据ID查询
     */
    ShopManager selectById(@Param("id") Integer id);


    /**
     * 根据商家ID查询
     */
    List<ShopManager> selectByShopId(@Param("shopId") Integer shopId);

    /**
     * 新增
     */
    int insert(@Param("param") ShopManager shopManager);

    /**
     * 更新
     */
    int update(@Param("data") ShopManager shopManager);

    /**
     * 根据ID删除
     */
    int deleteByIds(@Param("ids") List<Integer> ids);

    List<ShopManager> selectByIds(@Param("ids") Set<Integer> ids);

    /**
     * 根据商家ID和手机号查询
     *
     * @param shopId
     * @param phone
     * @return
     */
    ShopManager selectByShopIdAndPhone(@Param("shopId") Integer shopId, @Param("phone") String phone);

    int deleteByShopId(@Param("shopId") Integer shopId);
}
