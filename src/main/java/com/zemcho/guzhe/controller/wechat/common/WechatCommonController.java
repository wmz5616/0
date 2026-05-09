package com.zemcho.guzhe.controller.wechat.common;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.wechat.common.param.WechatSupermarketListParam;
import com.zemcho.guzhe.service.common.CommonService;
import com.zemcho.guzhe.service.file.FileService;
import com.zemcho.guzhe.service.sys.NoticeService;
import com.zemcho.guzhe.service.sys.SystemService;
import com.zemcho.guzhe.service.sys.ArticleService;
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
    private NoticeService noticeService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private ArticleService articleService;

    /**
     * C端获取消息公告列表
     * Tab切换，分页，按时间倒序
     */
    @RequestMapping("/notice/list")
    public Result getAppNoticeList(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        // 强制 C 端只能查询“已发布”的状态（status = 2 代表已发布）
        param.setSearchIntStatus(2);
        // 强制 C 端分页大小为原型图要求的 10
        param.setPageSize(10);
        return noticeService.getNoticesList(param);
    }

    /**
     * C端获取公告详情
     * 富文本长图文详情展示
     */
    @RequestMapping("/notice/info")
    public Result getAppNoticeInfo(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return noticeService.getNoticesInfo(param);
    }

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

    /**
     * 根据屏幕店地址的省市区获取商超下拉列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/supermarket/lists")
    public Result getSupermarketLists(@Validated @RequestBody WechatSupermarketListParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getWechatSupermarketLists(param);
    }

    /**
     * 动态获取后台基础配置 (Logo、系统名称、版权/备案号)
     * 前端调用此接口后，根据返回的 key渲染页面
     */
    @RequestMapping("/config/basic")
    public Result getBasicConfig() {
        return systemService.getBasicConfig();
    }

    /**
     * 动态获取后台启用的文章列表
     * 0表示启用
     */
    @RequestMapping("/article/list")
    public Result getArticleList(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        // 强制 C 端只允许查询“已启用”的文章状态（status = 0 代表启用）
        param.setSearchIntStatus(0);
        return articleService.select(param);
    }

    /**
     * 获取文章详情
     *
     */
    @RequestMapping("/article/info")
    public Result getArticleInfo(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return articleService.getArticleInfo(param);
    }
}