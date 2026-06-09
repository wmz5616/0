package com.zemcho.ddql.controller.recharge;

import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteOneParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.recharge.param.RechargeActivityParam;
import com.zemcho.ddql.controller.recharge.param.RechargeRequest;
import com.zemcho.ddql.entity.recharge.RechargeActivity;
import com.zemcho.ddql.service.recharge.RechargeActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 充值活动控制器
 *
 * @author Ryan
 */
@RestController
@RequestMapping("/recharge/activity")
public class RechargeActivityController {

    @Autowired
    private RechargeActivityService rechargeActivityService;

    /**
     * 新增/编辑充值活动
     * @param param
     * @param result
     * @return
     */
    @Log(description = "新增/编辑充值活动", module = "充值管理")
    @PostMapping("/save")
    public Result addRechargeActivity(@Validated @RequestBody RechargeRequest param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return rechargeActivityService.addRechargeActivity(param.getRechargeActivityList());
    }

    /**
     * 获取充值活动列表
     * @return
     */
    @RequestMapping("/lists")
    public Result selectList(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return rechargeActivityService.selectList(param);
    }

    /**
     * 根据id删除充值活动
     * @return
     */
    @Log(description = "删除充值活动", module = "充值管理")
    @RequestMapping("/delete")
    public Result deleteRechargeActivity(@Validated @RequestBody DeleteOneParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return rechargeActivityService.deleteRechargeActivity(param);
    }

    /**
     * 根据id进行充值活动排序
     */
    @Log(description = "排序充值活动", module = "充值管理")
    @RequestMapping("/sort")
    public Result sortRechargeActivity(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return rechargeActivityService.sortRechargeActivity(param);
    }
}
