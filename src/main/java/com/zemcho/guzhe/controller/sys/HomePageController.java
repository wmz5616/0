package com.zemcho.guzhe.controller.sys;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.entity.sys.HomePageBanner;
import com.zemcho.guzhe.service.sys.HomePageBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

// 首页轮播图
@RestController
@RequestMapping("/homePage")
public class HomePageController {

    @Autowired
    private HomePageBannerService homePageBannerService;


    // 首页轮播图相关
    @Log(description = "新增首页轮播图", module = "首页模块")
    @RequestMapping("/banner/insert")
    public Result insert(@Validated @RequestBody HomePageBanner param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return homePageBannerService.insert(param);
    }

    @Log(description = "更新首页轮播图", module = "首页模块")
    @RequestMapping("/banner/update")
    public Result update(@Validated @RequestBody HomePageBanner param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return homePageBannerService.update(param);
    }

    @Log(description = "删除首页轮播图", module = "首页模块")
    @RequestMapping("/banner/delete")
    public Result delete(@Validated @RequestBody DeleteParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return homePageBannerService.deleteByIds(new ArrayList<>(param.getDeleteIds()));
    }

    @RequestMapping("/banner/allList")
    public Result getBannerAllList() {
        return homePageBannerService.selectAll();
    }

    @RequestMapping("/banner/showList")
    public Result getBannerShowList() {
        return homePageBannerService.selectShowLists();
    }

    /**
     * 编辑首页轮播图状态
     *
     * @param param
     * @param result
     * @return
     */
    @Log(description = "编辑首页轮播图状态", module = "首页模块")
    @RequestMapping("/banner/status/set")
    public Result setStatus(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return homePageBannerService.setStatus(param);
    }

    /**
     * 修改首页轮播图顺序
     *
     * @param param
     * @param result
     * @return
     */
    @Log(description = "修改首页轮播图顺序", module = "首页模块")
    @RequestMapping("/banner/sort/set")
    public Result setPageBannerSort(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return homePageBannerService.setPageBannerSort(param);
    }
}
