package com.zemcho.ddql.controller.business;


import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.param.IndustryCategoryBatchParam;
import com.zemcho.ddql.entity.business.IndustryCategory;
import com.zemcho.ddql.service.business.IndustryCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/business/shop/cate")
public class IndustryCategoryController {

    @Autowired
    private IndustryCategoryService industryCategoryService;

    @Log(description = "删除行业类别", module = "商圈管理-新增/编辑店铺")
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

    @Log(description = "新增或者编辑行业类别", module = "商圈管理-新增/编辑店铺")
    @RequestMapping("/update")
    public Result updateIndustryCate(@Validated @RequestBody IndustryCategoryBatchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return industryCategoryService.update(param.getCategoryList());
    }

    @Log(description = "编辑行业类别排序", module = "商圈管理-新增/编辑店铺")
    @RequestMapping("/update/sort")
    public Result updateIndustryCateSort(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return industryCategoryService.updateSortByIds(param);
    }


}
