package com.zemcho.guzhe.service.product.impl;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteOneParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.product.param.CategoryParam;
import com.zemcho.guzhe.controller.product.param.ServeTypeRequest;
import com.zemcho.guzhe.entity.product.ProductCategory;
import com.zemcho.guzhe.mapper.product.ProductCategoryMapper;
import com.zemcho.guzhe.mapper.product.ProductMapper;
import com.zemcho.guzhe.service.product.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HXH
 */
@Service
public class ICategoryService implements CategoryService {

    @Autowired
    private ProductCategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;
    /**
     * 新增商品分类
     *
     * @param categoryList 商品分类列表
     * @return 新增结果
     */
    @Override
    public Result addCategory(ServeTypeRequest serveTypeRequest) {
        // 分类处理新增和更新
        List<ProductCategory> toInsert = new ArrayList<>();
        List<ProductCategory> toUpdate = new ArrayList<>();

        for(CategoryParam categoryParam : serveTypeRequest.getCategoryList()){
            ProductCategory category = new ProductCategory();
            BeanUtils.copyProperties(categoryParam, category);
            category.setShopId(serveTypeRequest.getShopId());
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
     * 获取商品分类列表
     *
     * @return
     */
    @Override
    public Result selectList(SearchParam param) {
        List<ProductCategory> categoryList = categoryMapper.selectList(param);
        return Result.success("操作成功", categoryList);
    }

    /**
     * 根据id进行商品分类删除
     */
    @Override
    public Result deleteCategory(DeleteOneParam param) {
        Integer categoryId = param.getDeleteId();
        Boolean ifExists =productMapper.ifExists(categoryId);
        if(ifExists){
            return Result.error("删除失败,该分类已被商品绑定");
        }
        ProductCategory category = categoryMapper.selectById(categoryId);
        categoryMapper.deleteById(categoryId);
        Integer deletedSort = category.getSort();
        if(deletedSort != null){
            categoryMapper.updateSortAfterDelete(deletedSort);
        }else{
            return Result.error("操作失败");
        }
        return Result.success("操作成功");
    }

    @Override
    public Result sortCategory(SearchParam param) {
        List<Integer> categoryIds = param.getSearchIds();
        if(categoryIds == null || categoryIds.isEmpty()){
            return Result.error("商品分类表不能为空");
        }
        // 根据id顺序批量更新sort字段
        List<ProductCategory> categoryList = new ArrayList<>();
        for (int i = 0; i < categoryIds.size(); i++) {
            ProductCategory category = categoryMapper.selectById(categoryIds.get(i));
            category.setSort(i + 1); // sort从1开始
            categoryList.add(category);
        }
        // 批量更新
        categoryMapper.updateBatch(categoryList);
        return Result.success("操作成功");
    }
}
