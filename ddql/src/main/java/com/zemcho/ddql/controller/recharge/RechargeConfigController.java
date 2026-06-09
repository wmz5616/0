package com.zemcho.ddql.controller.recharge;

import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.recharge.param.RechargeConfigParam;
import com.zemcho.ddql.entity.recharge.RechargeConfig;
import com.zemcho.ddql.service.recharge.RechargeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 充值配置控制器
 *
 * @author Ryan
 */
@RestController
@RequestMapping("/recharge/config")
public class RechargeConfigController {

    @Autowired
    private RechargeConfigService rechargeConfigService;

    @Log(description = "新增充值配置", module = "充值管理")
    @PostMapping("/add")
    public Result addRechargeConfig(@Validated @RequestBody RechargeConfigParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return rechargeConfigService.addRechargeConfig(param);
    }

    @Log(description = "编辑充值配置", module = "充值管理")
    @PostMapping("/update")
    public Result updateRechargeConfig(@Validated @RequestBody RechargeConfigParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return rechargeConfigService.updateRechargeConfig(param);
    }


    @PostMapping("/getInfo")
    public Result getRechargeConfigInfo(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return rechargeConfigService.getRechargeConfigInfo(param);
    }
}
