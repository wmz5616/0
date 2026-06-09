package com.zemcho.ddql.service.personalCenter;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.entity.personalCenter.Region;

import java.util.LinkedList;

public interface RegionService {
    // 根据当前地区id查询下层地区
    Result selectLowRegions(Integer id);

    // 根据当前地区的pid查询当前地区上层的地区
    Result selectRegionParent(Integer pid);

    // 根据当前地区id查询当前地区的内容
    Result selectRegionById(Integer id);

    /**
     * 根据地区id查询对应的地区数据--按等级顺序返回所有对应的上级地区数据
     *
     * @param id
     * @return
     */
    LinkedList<Region> selectRegionDataById(Integer id);
}
