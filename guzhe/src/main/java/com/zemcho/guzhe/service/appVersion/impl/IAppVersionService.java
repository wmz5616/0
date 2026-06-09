package com.zemcho.guzhe.service.appVersion.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.appVersion.param.AppVersionParam;
import com.zemcho.guzhe.entity.app.AppVersion;
import com.zemcho.guzhe.entity.app.AppVersionPushLog;
import com.zemcho.guzhe.entity.equipment.Equipment;
import com.zemcho.guzhe.mapper.appVersion.AppVersionMapper;
import com.zemcho.guzhe.mapper.appVersion.AppVersionPushLogMapper;
import com.zemcho.guzhe.mapper.equipment.EquipmentMapper;
import com.zemcho.guzhe.service.appVersion.AppVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class IAppVersionService implements AppVersionService {

    @Autowired
    private AppVersionMapper appVersionMapper;
    @Autowired
    private EquipmentMapper equipmentMapper;
    @Autowired
    private AppVersionPushLogMapper appVersionPushLogMapper;

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

    @Override
    public Result selectLog(SearchParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        String searchStrField1 = param.getSearchStrField1();//版本编号
        if(searchStrField1 == null && searchStrField1.isEmpty()){
            return Result.error("版本不存在");
        }
        List<AppVersionPushLog> list = appVersionPushLogMapper.selectLog(searchStrField1);
        PageInfo<AppVersionPushLog> pageInfo = new PageInfo<>(list);
        return Result.success("获取成功", pageInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result publish(SearchParam param) {
        Integer searchId = param.getSearchId();
        if(!appVersionMapper.ifExistById(searchId)){
            return Result.error("不存在该版本");
        }
        AppVersion appVersion = appVersionMapper.selectById(searchId);
        appVersion.setIsPublish(0);
        AppVersionParam appVersionParam = new AppVersionParam();
        BeanUtils.copyProperties(appVersion, appVersionParam);
        appVersionMapper.update(appVersionParam);
        return Result.success("发布成功");
    }

}
