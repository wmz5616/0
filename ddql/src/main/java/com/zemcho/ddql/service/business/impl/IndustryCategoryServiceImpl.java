package com.zemcho.ddql.service.business.impl;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.param.IndustryCategoryParam;
import com.zemcho.ddql.entity.business.IndustryCategory;
import com.zemcho.ddql.mapper.business.IndustryCategoryMapper;
import com.zemcho.ddql.service.business.IndustryCategoryService;
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
    public Result update(List<IndustryCategory> param) {
        Integer maxSort = industryCategoryMapper.selectMaxSort();
        if(maxSort==null){
            maxSort = 0;
        }
        for (IndustryCategory industryCategory : param) {
            if(industryCategory.getId()==null){
                //新增
                IndustryCategory industryCategory1 = new IndustryCategory();
                industryCategory1.setName(industryCategory.getName());
                industryCategory1.setSort(maxSort++);
                industryCategoryMapper.insert(industryCategory1);
            }else {
                //修改
                IndustryCategory industryCategory1 = industryCategoryMapper.selectById(industryCategory.getId());
                if (industryCategory1 == null) {
                    return Result.error("不存在ID为 " + industryCategory.getId() + " 的行业类别");
                }
                industryCategory1.setName(industryCategory.getName());
                industryCategoryMapper.update(industryCategory1);
            }
        }
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
