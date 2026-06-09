package com.zemcho.ddql.controller.business;

import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.ChangeOneParam;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.param.BusinessCircleParam;
import com.zemcho.ddql.service.business.BusinessCircleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/business/circle")
public class BusinessCircleController {
    @Autowired
    private BusinessCircleService businessCircleService;

    /**
     * 新增商圈
     *
     * @return
     */
    @Log(description = "新增商圈", module = "商圈管理-新增商圈")
    @RequestMapping("/save")
    public Result saveBusiness(@Validated @RequestBody BusinessCircleParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return businessCircleService.saveBusiness(param);
    }

    /**
     * 修改商圈
     *
     * @return
     */
    @Log(description = "修改商圈", module = "商圈管理-修改商圈")
    @RequestMapping("/update")
    public Result updateBusiness(@Validated @RequestBody BusinessCircleParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return businessCircleService.updateBusiness(param);
    }

    /**
     * 条件查询商圈列表
     *
     * @return
     */
    @RequestMapping("/lists")
    public Result selectList(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return businessCircleService.selectList(param);
    }

    /**
     * 根据id查询商圈信息
     */
    @RequestMapping("/selectById")
    public Result selectById(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return businessCircleService.selectById(param);
    }

    /**
     * 删除商圈
     *
     * @return
     */
    @Log(description = "删除商圈", module = "商圈管理-删除商圈")
    @RequestMapping("/delete")
    public Result deleteBusiness(@Validated @RequestBody DeleteParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return businessCircleService.deleteBusiness(param);
    }

    /**
     * 禁用/启用商圈
     */
    @Log(description = "禁用/启用商圈", module = "商圈管理-禁用/启用商圈")
    @RequestMapping("/status")
    public Result updateStatus(@Validated @RequestBody ChangeOneParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return businessCircleService.updateStatus(param);
    }

}
