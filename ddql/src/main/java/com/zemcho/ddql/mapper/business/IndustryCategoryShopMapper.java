package com.zemcho.ddql.mapper.business;


import com.zemcho.ddql.entity.business.IndustryCategoryShop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IndustryCategoryShopMapper {

    int insertBatch(@Param("list") List<IndustryCategoryShop> list);

    int deleteByShopIds(@Param("shopIds") List<Integer> shopIds);


    List<Integer> selectIndustryIdsByShopId(@Param("shopId") Integer shopId);
}
