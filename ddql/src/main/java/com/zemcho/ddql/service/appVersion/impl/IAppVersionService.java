package com.zemcho.ddql.service.appVersion.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.app.AppVersion;
import com.zemcho.ddql.mapper.appVersion.AppVersionMapper;
import com.zemcho.ddql.service.appVersion.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IAppVersionService implements AppVersionService {

    @Autowired
    private AppVersionMapper appVersionMapper;

    @Override
    public Result add(AppVersion data) {
        if (data.getSerialNumber() == null || data.getFileUrl() == null || data.getRelease() == null || data.getIsPublish() == null) {
            return Result.error("参数错误");
        }
        if (appVersionMapper.ifExistBySerialNumber(data.getSerialNumber(), 0)) {
            return Result.error("版本编号已存在");
        }
        data.setCreateTime(LocalDateTime.now());
        appVersionMapper.insert(data);
        return Result.success("操作成功");
    }

    @Override
    public Result update(AppVersion data) {
        if (data.getId() == null || !appVersionMapper.ifExistById(data.getId())) {
            return Result.error("id不能为空");
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
        List<AppVersion> list = appVersionMapper.select(param);
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
