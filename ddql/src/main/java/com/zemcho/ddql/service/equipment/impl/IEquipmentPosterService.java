package com.zemcho.ddql.service.equipment.impl;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.equipment.param.EquipmentPosterParam;
import com.zemcho.ddql.entity.equipment.EquipmentPoster;
import com.zemcho.ddql.mapper.equipment.EquipmentMapper;
import com.zemcho.ddql.mapper.equipment.EquipmentPosterMapper;
import com.zemcho.ddql.service.equipment.EquipmentPosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class IEquipmentPosterService implements EquipmentPosterService {
    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private EquipmentPosterMapper equipmentPosterMapper;

    @Override
    public Result save(EquipmentPosterParam param) {
        List<EquipmentPoster> list = param.getData();
        Integer equipmentId = list.get(0).getEquipmentId();
        if (!equipmentMapper.ifExistsById(equipmentId)) {
            return Result.error("设备不存在");
        }
        if (list.isEmpty()) {
            return Result.error("请选择要保存的海报");
        }
        // 默认状态 待投放
        Integer sort = 1;
        for (EquipmentPoster item : list) {
            item.setStatus(0);
            item.setSort(sort);
            sort++;
        }
        // 先删除旧海报
        equipmentPosterMapper.deleteByEquipmentId(equipmentId);
        // 再批量保存
        equipmentPosterMapper.insertBatch(list);
        return Result.success("保存成功");
    }

    @Override
    public Result select(SearchParam param) {
        Integer equipmentId = param.getSearchId();
        if (equipmentId == null) {
            return Result.error("参数异常");
        }
        List<EquipmentPoster> list = equipmentPosterMapper.selectByEquipmentId(equipmentId);
        return Result.success("查询成功", list);
    }

    /**
     * 修改设备海报顺序
     *
     * @param param
     * @return
     */
    @Override
    public Result setSort(SearchParam param) {
        List<Integer> ids = param.getSearchIds();
        if (ids == null || ids.isEmpty()) {
            return Result.error("参数异常");
        }

        Integer sort = 1;
        List<EquipmentPoster> updateList = new ArrayList<>();
        for (Integer id : ids) {
            EquipmentPoster updateData = new EquipmentPoster();
            updateData.setId(id);
            updateData.setSort(sort);
            updateList.add(updateData);
            sort++;
        }
        equipmentPosterMapper.updateBatch(updateList);

        return Result.success("操作成功");
    }

    /**
     * 删除设备海报
     *
     * @param param
     * @return
     */
    @Override
    public Result delete(DeleteParam param) {
        List<Integer> ids = new ArrayList<>(param.getDeleteIds());

        equipmentPosterMapper.deleteByIds(ids);

        return Result.success("操作成功");
    }

    // 用于定时更新海报的状态
    public void updateEquipmentPoster() {
        LocalDateTime now = LocalDateTime.now();
        // 获取所有待投放的海报
        List<EquipmentPoster> updateList = new ArrayList<>();
        List<EquipmentPoster> list = equipmentPosterMapper.selectByStatus(0);
        for (EquipmentPoster poster : list) {
            if (poster.getShowBeginTime().isBefore(now)) {
                EquipmentPoster updatePoster = new EquipmentPoster();
                updatePoster.setId(poster.getId());
                updatePoster.setStatus(1);
                updateList.add(updatePoster);
            }
        }
        if (!updateList.isEmpty()) {
            equipmentPosterMapper.updateBatch(updateList);
        }
        updateList.clear();
        // 获取所有投放中的海报
        list = equipmentPosterMapper.selectByStatus(1);
        for (EquipmentPoster poster : list) {
            if (poster.getShowEndTime().isBefore(now)) {
                EquipmentPoster updatePoster = new EquipmentPoster();
                updatePoster.setId(poster.getId());
                updatePoster.setStatus(2);
                updateList.add(updatePoster);
            }
        }
        if (!updateList.isEmpty()) {
            equipmentPosterMapper.updateBatch(updateList);
        }
    }
}
