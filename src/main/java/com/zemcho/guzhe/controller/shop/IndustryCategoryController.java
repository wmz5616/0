package com.zemcho.guzhe.controller.shop;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.entity.shop.IndustryCategory;
import com.zemcho.guzhe.service.shop.IndustryCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop/cate")
public class IndustryCategoryController {
    @Autowired
    private IndustryCategoryService industryCategoryService;

    @Log(description = "新增行业类别", module = "商家管理")
    @RequestMapping("/add")
    public Result addIndustryCate(@Validated @RequestBody IndustryCategory param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return industryCategoryService.addIndustryCate(param);
    }

    @Log(description = "删除行业类别", module = "商家管理")
    @RequestMapping("/del")
    public Result delIndustryCate(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return industryCategoryService.delByIds(param);
    }

    @RequestMapping("/get")
    public Result getIndustryCateList(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return industryCategoryService.getList();
    }

    @Log(description = "编辑行业类别", module = "商家管理")
    @RequestMapping("/update")
    public Result updateIndustryCate(@Validated @RequestBody IndustryCategory param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return industryCategoryService.updateById(param);
    }

    @Log(description = "编辑行业类别排序", module = "商家管理")
    @RequestMapping("/update/sort")
    public Result updateIndustryCateSort(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return industryCategoryService.updateSortByIds(param);
    }

}
