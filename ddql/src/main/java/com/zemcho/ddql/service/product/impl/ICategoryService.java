package com.zemcho.ddql.service.product.impl;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteOneParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.product.param.CategoryParam;
import com.zemcho.ddql.entity.product.Category;
import com.zemcho.ddql.entity.recharge.RechargeActivity;
import com.zemcho.ddql.mapper.product.CategoryMapper;
import com.zemcho.ddql.mapper.product.ProductMapper;
import com.zemcho.ddql.service.product.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ICategoryService implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 新增商品分类
     * @return
     */
    @Override
    public Result addCategory(List<CategoryParam> categoryList) {
        // 分类处理新增和更新
        List<Category> toInsert = new ArrayList<>();
        List<Category> toUpdate = new ArrayList<>();

        for(CategoryParam categoryParam : categoryList){
            Category category = new Category();
            BeanUtils.copyProperties(categoryParam, category);
            if (category.getId() == null || category.getId() == 0) {
                // 新增
                category.setCreateTime(LocalDateTime.now());
                category.setUpdateTime(LocalDateTime.now());
                toInsert.add(category);
            } else {
                // 更新
                toUpdate.add(category);
            }
        }

        if (!toInsert.isEmpty()) {
            categoryMapper.insertBatch(toInsert);
        }

        if (!toUpdate.isEmpty()) {
            categoryMapper.updateBatch(toUpdate);
        }

        return Result.success("操作成功");
    }

    /**
     * 根据id删除商品分类
     * @return
     */
    @Override
    public Result deleteCategory(DeleteOneParam param) {
        Integer categoryId = param.getDeleteId();
        Boolean ifExists =productMapper.ifExists(categoryId);
        if(ifExists){
            return Result.error("删除失败,该分类已被商品绑定");
        }
        Category category = categoryMapper.selectById(categoryId);
        categoryMapper.deleteById(categoryId);
        Integer deletedSort = category.getSort();
        if(deletedSort != null){
            categoryMapper.updateSortAfterDelete(deletedSort);
        }
        return Result.success("操作成功");
    }

    /**
     * 获取商品分类列表
     * @param param
     * @return
     */
    @Override
    public Result selectList(SearchParam param) {
        List<Category> categoryList = categoryMapper.selectList(param);
        return Result.success("操作成功", categoryList);
    }

    /**
     * 根据id进行商品分类排序
     * @param param
     * @return
     */
    @Override
    public Result sortCategory(SearchParam param) {
        List<Integer> categoryIds = param.getSearchIds();
        if(categoryIds == null || categoryIds.isEmpty()){
            return Result.error("商品分类表不能为空");
        }
        // 根据id顺序批量更新sort字段
        List<Category> categoryList = new ArrayList<>();
        for (int i = 0; i < categoryIds.size(); i++) {
            Category category = categoryMapper.selectById(categoryIds.get(i));
            category.setSort(i + 1); // sort从1开始
            categoryList.add(category);
        }
        // 批量更新
        categoryMapper.updateBatch(categoryList);
        return Result.success("操作成功");
    }
}
