package com.zemcho.guzhe.service.appVersion.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.appVersion.param.AppVersionParam;
import com.zemcho.guzhe.entity.app.AppVersion;
import com.zemcho.guzhe.mapper.appVersion.AppVersionMapper;
import com.zemcho.guzhe.service.appVersion.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IAppVersionService implements AppVersionService {

    @Autowired
    private AppVersionMapper appVersionMapper;

    @Override
    public Result add(AppVersionParam data) {
        //判断版本号是否重复
        if (appVersionMapper.ifExistBySerialNumber(data.getSerialNumber(), 0)) {
            return Result.error("版本编号已存在");
        }
        appVersionMapper.insert(data);
        return Result.success("操作成功");
    }

    @Override
    public Result update(AppVersionParam data) {
        if (data.getId() == null) {
            return Result.error("id不能为空");
        }
        if(!appVersionMapper.ifExistById(data.getId())){
            return Result.error("不存在该数据");
        }
        if (data.getSerialNumber() != null && appVersionMapper.ifExistBySerialNumber(data.getSerialNumber(),
                data.getId())) {
            return Result.error("版本编号已存在");
        }
        appVersionMapper.update(data);
        return Result.success("操作成功");
    }

    @Override
    public Result select(SearchParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<AppVersion> list = appVersionMapper.select();
        PageInfo<AppVersion> pageInfo = new PageInfo<>(list);
        return Result.success("获取成功", pageInfo);
    }

    @Override
    public Result delete(Integer id) {
        if (!appVersionMapper.ifExistById(id)) {
            return Result.error("不存在该数据");
        }
        appVersionMapper.delete(id);
        return Result.success("删除成功");
    }
}
