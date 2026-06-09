package com.zemcho.ddql.service.product;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteOneParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.product.param.CategoryParam;

import java.util.List;

public interface CategoryService {


    /**
     * 获取商品分类列表
     * @param param
     * @return
     */
    Result selectList(SearchParam param);

    /**
     * 新增商品分类
     * @return
     */
    Result addCategory(List<CategoryParam> categoryList);

    /**
     * 根据id删除商品分类
     * @return
     */
    Result deleteCategory(DeleteOneParam param);

    /**
     * 根据id进行商品分类排序
     * @param param
     * @return
     */
    Result sortCategory(SearchParam param);
}
