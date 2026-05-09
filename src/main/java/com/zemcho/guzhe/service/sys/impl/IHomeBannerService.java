package com.zemcho.guzhe.service.sys.impl;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.sys.param.HomeBannerParam;
import com.zemcho.guzhe.entity.sys.HomeBanner;
import com.zemcho.guzhe.mapper.sys.HomeBannerMapper;
import com.zemcho.guzhe.service.sys.HomeBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HXH
 */
@Service
public class IHomeBannerService implements HomeBannerService {

    @Autowired
    private HomeBannerMapper homeBannerMapper;

    @Override
    public Result insert(HomeBannerParam param) {
        //默认是全部用户：0
       if(param.getSortType()==null){
           param.setSortType(0);
       }
       //默认小程序:0
       if(param.getType()==null){
           param.setType(0);
       }
       //默认启用:1
       if(param.getStatus()==null){
           param.setStatus(1);
       }
       //判断名称是否唯一
        if(homeBannerMapper.ifExistsName(param.getName(),param.getBannerType())){
            return Result.error("名称重复!");
        }
       homeBannerMapper.insert(param);
        return Result.success("添加成功");
    }

    @Override
    public Result update(HomeBannerParam param) {
        if (param.getId() == null) {
            return Result.error("参数异常");
        }
        if (!homeBannerMapper.ifExist(param.getId(),param.getBannerType())) {
            return Result.error("数据不存在");
        }
        homeBannerMapper.update(param);
        return Result.success("更新成功");
    }

    @Override
    public Result deleteByIds(ArrayList<Integer> ids, Integer bannerType) {
        homeBannerMapper.deleteByIds(ids, bannerType);
        return Result.success("操作成功");
    }

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
        Integer bannerType = param.getBannerType();
        if(bannerType==null){
            return Result.error("参数异常!");
        }

        homeBannerMapper.updateStatusByIds(ids, status,bannerType);

        return Result.success("操作成功");
    }

    @Override
    public Result setPageBannerSort(SearchParam param) {
        List<Integer> ids = param.getSearchIds();
        if (ids == null || ids.isEmpty()) {
            return Result.error("参数异常");
        }
        Integer bannerType = param.getBannerType();
        if(bannerType==null){
            return Result.error("参数异常!");
        }
        Integer sort = 1;
        for (Integer id : ids) {
            HomeBannerParam data = new HomeBannerParam();
            data.setId(id);
            data.setSort(sort);
            data.setBannerType(bannerType);
            homeBannerMapper.update(data);
            sort++;
        }
        return Result.success("操作成功");
    }


    /**
     * 获取后台快捷入口配置
     * @return
     */
    @Override
    public Result selectLists() {
       List<HomeBanner>  list =homeBannerMapper.selectLists();
        return Result.success("操作成功", list);
    }

    /**
     * 获取后台banner图配置
     * @return
     */
    @Override
    public Result selectbanner() {
        List<HomeBanner>  list =homeBannerMapper.selectbanner();
        return Result.success("操作成功",list);
    }
}
