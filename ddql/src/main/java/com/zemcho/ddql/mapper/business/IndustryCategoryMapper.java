package com.zemcho.ddql.mapper.business;


import com.zemcho.ddql.controller.business.vo.ShopIndustryCategoryListVO;
import com.zemcho.ddql.entity.business.IndustryCategory;
import com.zemcho.ddql.entity.business.IndustryCategoryShop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IndustryCategoryMapper {

    Integer selectMaxSort();

    IndustryCategory selectById(@Param("id") Integer id);

    List<IndustryCategory> selectList();

    int insert(@Param("param") IndustryCategory industryCategory);

    int update(@Param("param") IndustryCategory industryCategory);

    int deleteByIds(@Param("ids") List<Integer> ids);

    List<IndustryCategory> selectByIds(@Param("ids") List<Integer> ids);

    List<ShopIndustryCategoryListVO> selectByShopIds(@Param("ids") List<Integer> shopIds);

}
