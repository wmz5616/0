package com.zemcho.guzhe.service.sys.impl;


import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.entity.sys.HomePageBanner;
import com.zemcho.guzhe.mapper.sys.HomePageBannerMapper;
import com.zemcho.guzhe.service.sys.HomePageBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IHomePageBannerService implements HomePageBannerService {

    @Autowired
    private HomePageBannerMapper homePageBannerMapper;

    @Override
    public Result insert(HomePageBanner data) {
        if (insertCheck(data)) {
            return Result.error("参数异常");
        }
        data.setCreateTime(LocalDateTime.now());

        Integer maxSort = homePageBannerMapper.selectMaxSort();
        if (maxSort == null) {
            data.setSort(1);
        } else {
            data.setSort(maxSort + 1);
        }

        homePageBannerMapper.insert(data);

        return Result.success("添加成功");
    }

    @Override
    public Result update(HomePageBanner data) {
        if (data.getId() == null) {
            return Result.error("参数异常");
        }
        if (!homePageBannerMapper.ifExist(data.getId())) {
            return Result.error("数据不存在");
        }
        homePageBannerMapper.update(data);
        return Result.success("更新成功");
    }

    @Override
    public Result selectAll() {
        List<HomePageBanner> list = homePageBannerMapper.selectAll();
        return Result.success("查询成功", list);
    }

    @Override
    public Result selectShowLists() {
        List<HomePageBanner> list = homePageBannerMapper.selectShowLists();
        return Result.success("查询成功", list);
    }

    @Override
    public Result deleteByIds(List<Integer> ids) {
        homePageBannerMapper.deleteByIds(ids);
        return Result.success("操作成功");
    }

    Boolean insertCheck(HomePageBanner data) {
        if (data == null) return true;
        if (data.getPic() == null) return true;
        if (data.getType() == null || (data.getType() != 0 && data.getType() != 1)) return true;
        if (data.getUrl() == null) return true;
        if (data.getStatus() == null || (data.getStatus() != 0 && data.getStatus() != 1)) return true;
        return false;
    }

    /**
     * 编辑首页轮播图状态
     *
     * @param param
     * @return
     */
    @Override
    public Result setStatus(SearchParam param) {
        List<Integer> ids = param.getSearchIds();
        if (ids == null || ids.isEmpty()) {
            return Result.error("参数异常");
        }

        Integer status = param.getSearchIntStatus();
        if (status == null) {
            return Result.error("参数异常!");
        }

        homePageBannerMapper.updateStatusByIds(ids, status);

        return Result.success("操作成功");
    }

    /**
     * 修改首页轮播图顺序
     *
     * @param param
     * @return
     */
    @Override
    public Result setPageBannerSort(SearchParam param) {
        List<Integer> ids = param.getSearchIds();
        if (ids == null || ids.isEmpty()) {
            return Result.error("参数异常");
        }

        Integer sort = 1;
        for (Integer id : ids) {
            HomePageBanner data = new HomePageBanner();
            data.setId(id);
            data.setSort(sort);
            homePageBannerMapper.update(data);
            sort++;
        }

        return Result.success("操作成功");
    }
}
