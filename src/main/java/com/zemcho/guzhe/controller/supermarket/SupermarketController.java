package com.zemcho.guzhe.controller.supermarket;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.ChangeParam;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.supermarket.param.SupermarketParam;
import com.zemcho.guzhe.service.supermarket.SupermarketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * @author Ryan
 */
@RestController
@RequestMapping("/supermarket")
public class SupermarketController {

    @Autowired
    private SupermarketService supermarketService;

    /**
     * 查询商超信息列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/select")
    @Log(description = "查询商超信息列表", module = "商超管理")
    public Result select(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return supermarketService.select(param);
    }

    /**
     * 新增商超信息
     *
     * @param data
     * @param result
     * @return
     */
    @RequestMapping("/add")
    @Log(description = "新增商超信息", module = "商超管理")
    public Result add(@Valid @RequestBody SupermarketParam data, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return supermarketService.add(data);
    }

    /**
     * 删除商超信息
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/delete")
    @Log(description = "删除商超信息", module = "商超管理")
    public Result delete(@Valid @RequestBody DeleteParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return supermarketService.delete(new ArrayList<>(param.getDeleteIds()));
    }

    /**
     * 编辑商超启用状态
     *
     * @param param
     * @param result
     * @return
     */
    @Log(description = "编辑商超启用状态", module = "商超管理")
    @RequestMapping("/status/set")
    public Result setStatus(@Validated @RequestBody ChangeParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return supermarketService.setStatus(param);
    }

    /**
     * 编辑商超
     *
     * @param data
     * @param result
     * @return
     */
    @RequestMapping("/update")
    @Log(description = "编辑商超", module = "商超管理")
    public Result update(@Valid @RequestBody SupermarketParam data, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return supermarketService.update(data);
    }

    /**
     * 获取已启用商超信息
     *
     * @return
     */
    @RequestMapping("/get")
    @Log(description = "获取已启用商超信息", module = "商超管理")
    public Result get() {
        return supermarketService.get();
    }
}
