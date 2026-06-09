package com.zemcho.ddql.controller.statistic;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.controller.statistic.param.MonitorParam;
import com.zemcho.ddql.service.statistic.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @title: MonitorController
 * @Description:
 * @Date: 2025/11/5 19:30
 */
@RestController
@RequestMapping("/monitor")
public class MonitorController {
    @Autowired
    private MonitorService service;

    /**
     * 系统数据统计
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/system/stat")
    public Result systemStat(@Validated @RequestBody MonitorParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.systemStat(param);
    }

    /**
     * 平台用户统计
     *
     * @return
     */
    @RequestMapping("/user/stat")
    public Result userStat() {
        return service.userStat();
    }

    /**
     * 用户活跃度统计
     *
     * @return
     */
    @RequestMapping("/active/stat")
    public Result activeStat() {
        return service.activeStat();
    }

    /**
     * 平台流量情况
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/visit/trend/stat")
    public Result visitTrendStat(@Validated @RequestBody MonitorParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.visitTrendStat(param);
    }

    /**
     * 场地打卡量排行榜
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/place/check_in/rank/lists")
    public Result placeCheckInRankLists(@Validated @RequestBody MonitorParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.placeCheckInRankLists(param);
    }
}
