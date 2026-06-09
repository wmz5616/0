package com.zemcho.ddql.service.personalCenter.impl;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.entity.personalCenter.Region;
import com.zemcho.ddql.mapper.personalCenter.RegionMapper;
import com.zemcho.ddql.service.personalCenter.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class IRegionService implements RegionService {


    @Autowired
    private RegionMapper regionMapper;

    @Override
    public Result selectLowRegions(Integer id) {
        List<Region> regions = regionMapper.selectLowRegions(id);
        return Result.success("获取成功", regions);
    }

    @Override
    public Result selectRegionParent(Integer id) {
        Region region = regionMapper.selectRegionParent(id);
        return Result.success("获取成功", region);
    }

    @Override
    public Result selectRegionById(Integer id) {
        Region region = regionMapper.selectById(id);
        return Result.success("获取成功", region);
    }

    /**
     * 根据地区id查询对应的地区数据--按等级顺序返回所有对应的上级地区数据
     *
     * @param id
     * @return
     */
    @Override
    public LinkedList<Region> selectRegionDataById(Integer id) {
        LinkedList<Region> data = new LinkedList<>();

        while (true) {
            Region region = regionMapper.selectById(id);
            if (region == null) {
                break;
            }
            data.addFirst(region);
            id = region.getPid();
            if (id == 0 || id == 100000) {
                break;
            }
        }

        return data;
    }
}
