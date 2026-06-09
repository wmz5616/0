package com.zemcho.ddql.controller.wechat.common;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.team.param.TeamSearchParam;
import com.zemcho.ddql.service.business.BusinessCircleService;
import com.zemcho.ddql.service.business.IndustryCategoryService;
import com.zemcho.ddql.service.checkInSettings.CheckInTypeService;
import com.zemcho.ddql.service.common.CommonService;
import com.zemcho.ddql.service.file.FileService;
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

    @Autowired
    private CheckInTypeService checkInTypeService;

    @Autowired
    private BusinessCircleService businessCircleService;

    @Autowired
    private IndustryCategoryService industryCategoryService;

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
     * 获取团体下拉列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/team/lists")
    public Result getTeamLists(@Validated @RequestBody TeamSearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getTeamLists(param);
    }

    /**
     * 获取用户端--文章列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/article/list")
    public Result getArticleList(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getWechatArticleList(param);
    }

    /**
     * 获取用户端--公告列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/notice/list")
    public Result getNoticeList(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getWechatNoticeList(param);
    }

    /**
     * 获取打卡类型列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/check_in_type/list")
    public Result getCheckInTypeList(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return checkInTypeService.getCheckInTypeList(param);
    }


    /**
     * 获取商超下拉列表
     *
     * @return 商圈下拉列表
     */
    @RequestMapping("/circle/list")
    public Result getBusinessCircleList(@Validated @RequestBody SearchParam param, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return businessCircleService.selectCommonList(param);
    }

    /**
     * 获取行业类别列表
     *
     * @return 行业类别列表
     */
    @RequestMapping("/industry/list")
    public Result getIndustryCategoryList() {
        return industryCategoryService.getList();
    }
}
