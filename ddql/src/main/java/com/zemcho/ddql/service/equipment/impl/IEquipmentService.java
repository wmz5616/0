package com.zemcho.ddql.service.equipment.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.ChangeParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.equipment.vo.EquipmentVo;
import com.zemcho.ddql.entity.equipment.Equipment;
import com.zemcho.ddql.entity.equipment.EquipmentLog;
import com.zemcho.ddql.mapper.checkInSettings.CheckInPlaceMapper;
import com.zemcho.ddql.mapper.equipment.EquipmentLogMapper;
import com.zemcho.ddql.mapper.equipment.EquipmentMapper;
import com.zemcho.ddql.service.equipment.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class IEquipmentService implements EquipmentService {
    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private EquipmentLogMapper equipmentLogMapper;

    @Autowired
    private CheckInPlaceMapper checkInPlaceMapper;

    @Override
    public Result add(Equipment data) {
        // 检查参数
        if (data.getCheckInPlaceId() == null || data.getSerialNumber() == null
                || data.getEnableStatus() == null) {
            return Result.error("参数错误");
        }
        if (data.getEnableStatus() != 0 && data.getEnableStatus() != 1) {
            return Result.error("参数错误");
        }
        if (!checkInPlaceMapper.ifExistsById(data.getCheckInPlaceId())) {
            return Result.error("打卡地点不存在");
        }
        if (equipmentMapper.ifExistsBySerialNumber(data.getSerialNumber(), 0)) {
            return Result.error("设备编号已存在");
        }
        data.setOnlineStatus(1);
        data.setEnableStatus(0);
        data.setCreateTime(LocalDateTime.now());
        equipmentMapper.insert(data);
        return Result.success("操作成功");
    }

    @Override
    public Result update(Equipment data) {
        if (data.getId() == null) {
            return Result.error("参数错误");
        }
        if (data.getCheckInPlaceId() != null && !checkInPlaceMapper.ifExistsById(data.getCheckInPlaceId())) {
            return Result.error("打卡地点不存在");
        }
        if (data.getSerialNumber() != null
                && equipmentMapper.ifExistsBySerialNumber(data.getSerialNumber(), data.getId())) {
            return Result.error("设备编号已存在");
        }
        equipmentMapper.update(data);
        return Result.success("操作成功");
    }

    @Override
    public Result select(SearchParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<EquipmentVo> list = equipmentMapper.select(param);
        PageInfo<EquipmentVo> pageInfo = new PageInfo<>(list);
        return Result.success("获取成功", pageInfo);
    }

    @Override
    public Result delete(List<Integer> deleteIds) {
        equipmentMapper.delete(deleteIds);
        return Result.success("删除成功");
    }

    /**
     * 编辑设备状态
     *
     * @param param
     * @return
     */
    @Override
    public Result setStatus(ChangeParam param) {
        List<Integer> ids = new ArrayList<>(param.getChangeIds());

        equipmentMapper.updateStatusByIds(ids, param.getStatus());

        return Result.success("操作成功");
    }

    /**
     * 获取设备日志列表
     *
     * @param param
     * @return
     */
    @Override
    public Result logLists(SearchParam param) {
        Integer equipmentId = param.getSearchId();
        if (equipmentId == null) {
            return Result.error("参数错误");
        }

        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<EquipmentLog> list = equipmentLogMapper.selectLists(param);
        PageInfo<EquipmentLog> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 获取场所下的设备信息
     *
     * @param param
     * @return
     */
    @Override
    public Result getEquipmentByPlace(SearchParam param) {
        Integer placeId = param.getSearchId();
        if (placeId == null) {
            return Result.error("参数错误");
        }

        Equipment equipment = equipmentMapper.selectByPlaceId(placeId);

        return Result.success("获取成功", equipment);
    }
}
