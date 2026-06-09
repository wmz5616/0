package com.zemcho.ddql.service.checkInSettings.impl;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.checkInSettings.vo.CheckInTypeVo;
import com.zemcho.ddql.controller.checkInSettings.param.CheckInTypeParam;
import com.zemcho.ddql.entity.checkInSettings.CheckInType;
import com.zemcho.ddql.mapper.checkInSettings.CheckInTypeMapper;
import com.zemcho.ddql.service.checkInSettings.CheckInTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ICheckInTypeService implements CheckInTypeService {
    @Autowired
    private CheckInTypeMapper checkInTypeMapper;

    @Override
    public Result add(CheckInTypeParam param) {
        Integer maxSort = checkInTypeMapper.selectMaxSort();
        if (maxSort == null) {
            maxSort = 0;
        }
        Integer sort = maxSort + 1;

        // 将以List转成以；分隔的字符串
        String images = String.join(";", param.getImages());
        CheckInType checkInType = new CheckInType(param.getId(), images, param.getName(), param.getOtherName(),
                param.getStatus(), param.getInstruction(), sort);
        checkInTypeMapper.insert(checkInType);
        return Result.success("添加成功");
    }

    @Override
    public Result update(CheckInTypeParam param) {
        if (param.getId() == null) {
            return Result.error("id不能为空");
        }
        // 将以List转成以；分隔的字符串
        String images = String.join(";", param.getImages());
        CheckInType checkInType = new CheckInType(param.getId(), images, param.getName(), param.getOtherName(),
                param.getStatus(), param.getInstruction(), null);
        checkInTypeMapper.update(checkInType);
        return Result.success("修改成功");
    }

    @Override
    public Result getCheckInTypeList(SearchParam param) {
        List<CheckInTypeVo> list = checkInTypeMapper.selectList(param);
        for (CheckInTypeVo checkInTypeVo : list) {
            // 将以;分隔的字符串转为List
            checkInTypeVo.setImagesList(Arrays.stream(checkInTypeVo.getImages().split(";")).toList());
        }
        return Result.success("查询成功", list);
    }

    @Override
    public Result delete(List<Integer> deleteIds) {
        checkInTypeMapper.delete(deleteIds);
        return Result.success("删除成功");
    }

    /**
     * 修改打卡类型顺序
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
        for (Integer id : ids) {
            CheckInType updateData = new CheckInType();
            updateData.setId(id);
            updateData.setSort(sort);
            checkInTypeMapper.update(updateData);
            sort++;
        }

        return Result.success("操作成功");
    }
}
