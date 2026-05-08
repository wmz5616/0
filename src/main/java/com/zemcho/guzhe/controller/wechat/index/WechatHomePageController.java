package com.zemcho.guzhe.controller.wechat.index;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.service.sys.HomeBannerService;
import com.zemcho.guzhe.service.sys.HomePageBannerService;
import com.zemcho.guzhe.service.sys.NoticeService;
import com.zemcho.guzhe.service.sys.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HXH
 */
@RestController
@RequestMapping("/wechat")
public class WechatHomePageController {

    @Autowired
    private HomePageBannerService service;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private HomeBannerService homeBannerService;

    @Autowired
    private SystemService systemService;

    /**
     * 获取首页的轮播图
     *
     * @return
     */
    @RequestMapping("/homePage")
    public Result getHomePage() {
        return service.selectShowLists();
    }
    /**
     * 获取置顶公告
     *
     * @return
     */
    @RequestMapping("/top/notice")
    public Result getTopNotice() {
        return noticeService.getTopNotice();
    }

    /**
     * 获取后台快捷入口配置显示
     *
     * @return
     */
    @RequestMapping("/config/show")
    public Result getConfigShow() {
     return homeBannerService.selectLists();
    }

    /**
     * 获取后台banner图配置
     *
     * @return
     */
    @RequestMapping("/config/banner")
    public Result getConfigBanner() {
        return homeBannerService.selectbanner();
    }

    /**
     * 获取系统基础配置信息
     *
     * @return
     */
    @RequestMapping("/basic/config")
    public Result getBasicConfig() {
        return systemService.getBasicConfig();
    }
}
