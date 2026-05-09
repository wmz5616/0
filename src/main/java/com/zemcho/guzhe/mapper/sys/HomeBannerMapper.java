package com.zemcho.guzhe.mapper.sys;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.sys.param.HomeBannerParam;
import com.zemcho.guzhe.entity.sys.HomeBanner;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HXH
 */
public interface HomeBannerMapper {

    int insert(@Param("data") HomeBannerParam data);

    @Select("select case when count(*) > 0 then true else false end from home_banner where name = #{name} and delete_time is null and banner_type = #{bannerType}")
    Boolean ifExistsName(@Param("name") String name,@Param("bannerType") Integer bannerType);


    @Select("select count(*) from home_banner where id = #{id} and banner_type = #{bannerType}")
    Boolean ifExist(@Param("id") Integer id,@Param("bannerType") Integer bannerType);

    void update(@Param("data")HomeBannerParam data);

    void deleteByIds(@Param("ids")ArrayList<Integer> ids,@Param("bannerType") Integer bannerType);

    void updateStatusByIds(@Param("ids")List<Integer> ids, @Param("status") Integer status,@Param("bannerType") Integer bannerType);

    List<HomeBanner> selectLists();

    List<HomeBanner> selectbanner();

}
