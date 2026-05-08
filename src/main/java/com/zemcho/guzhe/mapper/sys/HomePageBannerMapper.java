package com.zemcho.guzhe.mapper.sys;

import com.zemcho.guzhe.entity.sys.HomePageBanner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@Mapper
public interface HomePageBannerMapper {

    @Select("select count(*) from home_page_banner where id = #{id} ")
    Boolean ifExist(@Param("id") Integer id);

    int insert(@Param("data") HomePageBanner data);

    int update(@Param("data") HomePageBanner data);

    // 后台查找所有的轮播图
    @Select("select * from home_page_banner order by sort asc, id asc")
    List<HomePageBanner> selectAll();

    // 前台展示的轮播图
    @Select("select * from home_page_banner where status = 1 order by sort asc, id asc")
    List<HomePageBanner> selectShowLists();

    void deleteByIds(@Param("ids") List<Integer> ids);

    /**
     * 批量更新状态
     *
     * @param ids
     * @param status
     * @return
     */
    Integer updateStatusByIds(@Param("ids") Collection<Integer> ids, @Param("status") Integer status);

    /**
     * 查询最大排序
     *
     * @return
     */
    Integer selectMaxSort();
}
