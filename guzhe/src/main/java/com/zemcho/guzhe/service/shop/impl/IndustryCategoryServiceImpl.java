package com.zemcho.guzhe.service.shop.impl;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.entity.shop.IndustryCategory;
import com.zemcho.guzhe.mapper.shop.IndustryCategoryMapper;
import com.zemcho.guzhe.service.shop.IndustryCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IndustryCategoryServiceImpl implements IndustryCategoryService {
    @Autowired
    private IndustryCategoryMapper industryCategoryMapper;

    @Override
    public Result getList() {
        List<IndustryCategory> industryCategories = industryCategoryMapper.selectList();
        return Result.success("操作成功", industryCategories);
    }

    @Override
    public Result delByIds(SearchParam param) {
        if (param.getSearchIds() == null || param.getSearchIds().isEmpty()) {
            return Result.error("参数错误");
        }
        industryCategoryMapper.deleteByIds(param.getSearchIds());
        return Result.success("操作成功");
    }

    @Override
    public Result updateById(IndustryCategory param) {
        if (param.getId() == null) {
            return Result.error("参数错误");
        }
        IndustryCategory industryCategory = industryCategoryMapper.selectById(param.getId());
        if (industryCategory == null) {
            return Result.error("参数错误");
        }
        industryCategoryMapper.update(param);
        return Result.success("操作成功");
    }

    @Override
    public Result addIndustryCate(IndustryCategory param) {
        // 新增行业类别
        Integer sort = industryCategoryMapper.selectMaxSort();
        if (sort != null) {
            param.setSort(sort + 1);
        } else {
            param.setSort(1);
        }
        industryCategoryMapper.insert(param);
        return Result.success("操作成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateSortByIds(SearchParam param) {
        // 更新类别排序
        if (param.getSearchIds() == null || param.getSearchIds().isEmpty()) {
            return Result.error("参数错误");
        }
        List<IndustryCategory> industryCategories = industryCategoryMapper.selectByIds(param.getSearchIds());
        if (industryCategories == null || industryCategories.isEmpty() || industryCategories.size() != param.getSearchIds().size()) {
            return Result.error("参数错误");
        }

        List<Integer> ids = param.getSearchIds();
        Integer sort = 1;

        for (Integer id : ids) {
            IndustryCategory industryCategory = new IndustryCategory();
            industryCategory.setSort(sort);
            industryCategory.setId(id);
            industryCategoryMapper.update(industryCategory);
            sort++;
        }
        return Result.success("操作成功");
    }
}
