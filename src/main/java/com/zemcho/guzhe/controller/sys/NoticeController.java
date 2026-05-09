package com.zemcho.guzhe.controller.sys;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.sys.param.NoticeSaveParam;
import com.zemcho.guzhe.service.sys.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ryan
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    /**
     * 新增通知公告
     *
     * @return
     */
    @Log(description = "新增通知公告", module = "通知公告管理")
    @RequestMapping("/add")
    public Result addNotices(@Validated @RequestBody NoticeSaveParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return noticeService.addNotices(param);
    }

    @Log(description = "编辑通知公告", module = "通知公告管理")
    @RequestMapping("/update")
    public Result updateNotices(@Validated @RequestBody NoticeSaveParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return noticeService.updateNotices(param);
    }

    @Log(description = "删除通知公告", module = "通知公告管理")
    @RequestMapping("/delete")
    public Result deleteNotices(@Validated @RequestBody DeleteParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return noticeService.deleteNotices(param);
    }

    /**
     * 获取通知公告详情
     *
     * @return
     */
    @RequestMapping("/getInfo")
    public Result getNoticesInfo(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return noticeService.getNoticesInfo(param);
    }

    /**
     * 获取通知公告列表
     *
     * @return
     */
    @RequestMapping("/getNoticeList")
    public Result getNoticesList(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return noticeService.getNoticesList(param);
    }

    /**
     * 设置通知公告置顶状态
     *
     * @param param
     * @param result
     * @return
     */
    @Log(description = "设置通知公告置顶状态", module = "通知公告管理")
    @RequestMapping("/top/set")
    public Result setTopStatus(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return noticeService.setTopStatus(param);
    }
}