package com.zemcho.guzhe.controller.wechat.common;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.service.common.CommonService;
import com.zemcho.guzhe.service.file.FileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @title: WechatCommonController
 * @Description:
 * @Date: 2025/10/15 17:00
 */
@RestController
@RequestMapping("/wechat/common")
public class WechatCommonController {
    @Autowired
    FileService fileService;

    @Autowired
    CommonService service;

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    @PostMapping("/file/upload")
    public Result upload(MultipartFile file) {
        return fileService.upload(file);
    }

    /**
     * 根据当前地区id查询下级地区
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/region/low/list")
    public Result getLowRegionList(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return service.getLowRegionList(param);
    }

    /**
     * 根据地区id查询上级地区
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/region/parent/info")
    public Result getRegionParentInfo(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return service.getRegionParentInfo(param);
    }

    /**
     * 根据地区id查询地区信息
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/region/info")
    public Result getRegionInfo(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return service.getRegionInfo(param);
    }

    /**
     * 获取用户下拉列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/user/lists")
    public Result getUserLists(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getUserLists(param);
    }
}
