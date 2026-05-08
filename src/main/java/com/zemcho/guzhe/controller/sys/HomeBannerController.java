package com.zemcho.guzhe.controller.sys;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.sys.param.HomeBannerParam;
import com.zemcho.guzhe.service.sys.HomeBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * @author HXH
 */
//首页快捷入口和首页banner图
@RestController
@RequestMapping("/homeBanner")
public class HomeBannerController {

    @Autowired
    private HomeBannerService homeBannerService;


    /**
     * 新增
     *
     * @param param
     * @param result
     * @return
     */
    @Log(description = "新增", module = "首页模块")
    @RequestMapping("/insert")
    public Result insert(@Validated @RequestBody HomeBannerParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return homeBannerService.insert(param);
    }

    /**
     * 更新
     *
     * @param param
     * @param result
     * @return
     */
    @Log(description = "更新", module = "首页模块")
    @RequestMapping("/update")
    public Result update(@Validated @RequestBody HomeBannerParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return homeBannerService.update(param);
    }

    /**
     * 删除
     *
     * @param param
     * @param result
     * @return
     */
    @Log(description = "删除", module = "首页模块")
    @RequestMapping("/delete")
    public Result delete(@Validated @RequestBody DeleteParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return homeBannerService.deleteByIds(new ArrayList<>(param.getDeleteIds()),param.getBannerType());
    }
    /**
     * 编辑状态
     *
     * @param param
     * @param result
     * @return
     */
    @Log(description = "编辑状态", module = "首页模块")
    @RequestMapping("/status/set")
    public Result setStatus(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return homeBannerService.setStatus(param);
    }

    /**
     * 修改顺序
     *
     * @param param
     * @param result
     * @return
     */
    @Log(description = "修改顺序", module = "首页模块")
    @RequestMapping("/sort/set")
    public Result setPageBannerSort(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return homeBannerService.setPageBannerSort(param);
    }
}
