package com.zemcho.guzhe.controller.wechat.product;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteOneParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.product.param.ServeTypeRequest;
import com.zemcho.guzhe.service.wechat.product.WechatCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HXH
 */
//小程序商品分类
@RequestMapping("/wechat/product/category")
@RestController
public class WechatCategoryController {
    @Autowired
    private WechatCategoryService categoryService;
    /**
     * 新增商品分类
     * @return
     */
    @RequestMapping("/add")
    public Result addCategory(@Validated @RequestBody ServeTypeRequest param, BindingResult result,
                              @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return categoryService.addCategory(param,token);
    }

    /**
     * 获取商品分类列表
     * @return
     */
    @RequestMapping("/lists")
    public Result selectList(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return categoryService.selectList(param);
    }


    /**
     * 根据id删除商品分类
     * @return
     */
    @RequestMapping("/delete")
    public Result deleteOrgType(@Validated @RequestBody DeleteOneParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return categoryService.deleteCategory(param);
    }

    /**
     * 根据id进行商品分类排序
     */
    @RequestMapping("/sort")
    public Result sortCategory(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return categoryService.sortCategory(param);
    }
}
