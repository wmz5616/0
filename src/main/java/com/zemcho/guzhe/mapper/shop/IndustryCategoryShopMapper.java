package com.zemcho.guzhe.mapper.shop;

import com.zemcho.guzhe.entity.shop.IndustryCategoryShop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IndustryCategoryShopMapper {
    int insertBatch(@Param("list") List<IndustryCategoryShop> list);

    int deleteByShopIds(@Param("shopIds") List<Integer> shopIds);

    List<Integer> selectIndustryIdsByShopId(@Param("searchId") Integer searchId);
}
