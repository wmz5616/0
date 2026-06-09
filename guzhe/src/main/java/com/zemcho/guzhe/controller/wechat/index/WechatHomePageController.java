package com.zemcho.guzhe.controller.wechat.index;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.service.index.WechatHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HXH
 */
@RestController
@RequestMapping("/wechat/open")
public class WechatHomePageController {
    @Autowired
    private WechatHomeService wechatHomeService;

    /**
     * 获取首页的轮播图
     *
     * @return
     */
    @RequestMapping("/homePage")
    public Result getHomePage() {
        return wechatHomeService.selectShowLists();
    }
    /**
     * 获取置顶公告
     *
     * @return
     */
    @RequestMapping("/top/notice")
    public Result getTopNotice() {
        return wechatHomeService.getTopNotice();
    }

    /**
     * 获取后台快捷入口配置显示
     *
     * @return
     */
    @RequestMapping("/config/show")
    public Result getConfigShow(@RequestHeader("token") String token) {
        return wechatHomeService.selectLists(token);
    }

    /**
     * 获取后台banner图配置
     *
     * @return
     */
    @RequestMapping("/config/banner")
    public Result getConfigBanner(@RequestHeader("token") String token) {
        return wechatHomeService.selectbanner(token);
    }

    /**
     * 获取系统基础配置信息
     *
     * @return
     */
    @RequestMapping("/basic/config")
    public Result getBasicConfig() {
        return wechatHomeService.getBasicConfig();
    }
}
