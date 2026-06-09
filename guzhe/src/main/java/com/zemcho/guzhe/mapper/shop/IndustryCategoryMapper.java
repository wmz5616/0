package com.zemcho.guzhe.mapper.shop;

import com.zemcho.guzhe.controller.shop.vo.ShopIndustryCategoryListVO;
import com.zemcho.guzhe.entity.shop.IndustryCategory;
import com.zemcho.guzhe.entity.shop.ShopAuditIndustry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IndustryCategoryMapper {

    Integer selectMaxSort();

    IndustryCategory selectById(@Param("id") Integer id);

    List<IndustryCategory> selectList();

    int insert(@Param("data") IndustryCategory data);

    int update(@Param("data") IndustryCategory data);

    int deleteByIds(@Param("ids") List<Integer> ids);

    List<IndustryCategory> selectByIds(@Param("ids") List<Integer> ids);

    List<ShopIndustryCategoryListVO> selectByShopIds(@Param("ids") List<Integer> shopIds);


}
