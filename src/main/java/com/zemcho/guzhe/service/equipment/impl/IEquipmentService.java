package com.zemcho.guzhe.service.equipment.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.ChangeParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.equipment.param.EquipmentRentalDisplayParam;
import com.zemcho.guzhe.controller.equipment.param.EquipmentParam;
import com.zemcho.guzhe.controller.equipment.vo.EquipmentRentalDisplayVo;
import com.zemcho.guzhe.controller.equipment.vo.EquipmentVo;
import com.zemcho.guzhe.entity.equipment.Equipment;
import com.zemcho.guzhe.entity.equipment.EquipmentLog;
import com.zemcho.guzhe.mapper.equipment.EquipmentLogMapper;
import com.zemcho.guzhe.mapper.equipment.EquipmentMapper;
import com.zemcho.guzhe.service.equipment.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class IEquipmentService implements EquipmentService {
    private static final int DEFAULT_SLOT_TOTAL = 6;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private EquipmentLogMapper equipmentLogMapper;

    @Override
    public Result add(EquipmentParam data) {
        //默认是离线状态
        if(data.getOnlineStatus() == null){
            data.setOnlineStatus(0);
        }
        //如果启动状态为空则设置为已启用
        if(data.getStatus()==null){
            data.setStatus(1);
        }
        if (data.getStatus() != 0 && data.getStatus() != 1) {
            return Result.error("参数错误");
        }
        //如果排序为空则设置为0
        if(data.getSort() == null){
            data.setSort(0);
        }
        //校验排序不能为负数
        if(data.getSort() < 0){
            return Result.error("排序不能小于0");
        }
        if (data.getMoney() < 0){
            return Result.error("店位每月租金不能小于0");
        }

        //校验设备编号是否唯一
        if(equipmentMapper.ifExistsById(data.getSerialNumber())){
            return Result.error("设备编号已存在");
        }
        equipmentMapper.insert(data);
        return Result.success("操作成功");
    }

    @Override
    public Result update(EquipmentParam data) {
        if (data.getId() == null) {
            return Result.error("参数错误");
        }
        if (data.getSerialNumber() != null
                && equipmentMapper.ifExistsBySerialNumber(data.getSerialNumber(), data.getId())) {
            return Result.error("设备编号已存在");
        }
        if (data.getMoney()!=null&&data.getMoney()< 0){
            return Result.error("店位每月租金不能小于0");
        }
        //校验排序不能为负数
        if(data.getSort()!=null&&data.getSort() < 0){
            return Result.error("排序不能小于0");
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
     * 编辑设备启用状态
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
     * 获取设备店位租用展示列表
     *
     * @param param
     * @return
     */
    @Override
    public Result rentalDisplayLists(EquipmentRentalDisplayParam param) {
        YearMonth queryMonth;
        // 判断月份是否为空，为空则默认当前月
        try {
            queryMonth = parseQueryMonth(param.getMonth());
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }

        List<EquipmentRentalDisplayVo> list =
                equipmentMapper.selectRentalDisplayLists(param, queryMonth.getYear(), queryMonth.getMonthValue());
        for (EquipmentRentalDisplayVo item : list) {
            if (item.getSlotTotal() == null) {
                item.setSlotTotal(DEFAULT_SLOT_TOTAL);
            }
            if (item.getUsedCount() == null) {
                item.setUsedCount(0);
            }
            item.setRemainingCount(Math.max(item.getSlotTotal() - item.getUsedCount(), 0));
            if (item.getMerchantNames() == null) {
                item.setMerchantNames("");
            }
        }

        return Result.success("获取成功", list);
    }

    /**
     * 解析查询月份，为空时默认当前月份。
     */
    private YearMonth parseQueryMonth(String month) {
        if (month == null || month.trim().isEmpty()) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(month.trim(), MONTH_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("查询月份格式错误");
        }
    }

}
