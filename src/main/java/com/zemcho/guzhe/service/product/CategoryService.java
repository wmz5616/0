package com.zemcho.guzhe.service.product;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteOneParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.product.param.CategoryParam;
import com.zemcho.guzhe.controller.product.param.ServeTypeRequest;

import java.util.List;

/**
 * @author HXH
 */
public interface CategoryService {
    /**
     * 新增商品分类
     *
     * @param categoryList 商品分类列表
     * @return 新增结果
     */
    Result addCategory(ServeTypeRequest serveTypeRequest);

    /**
     * 获取商品分类列表
     *
     * @return
     */
    Result selectList(SearchParam param);

    /**
     * 根据id进行商品分类排序
     */
    Result deleteCategory(DeleteOneParam param);

    Result sortCategory(SearchParam param);
}
