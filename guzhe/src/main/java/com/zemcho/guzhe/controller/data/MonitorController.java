package com.zemcho.guzhe.controller.data;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.data.param.MonitorParam;
import com.zemcho.guzhe.controller.product.param.ProductSearchParam;
import com.zemcho.guzhe.service.data.MonitorService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HXH
 */
//后台数据总览
@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    private MonitorService service;

    @Autowired
    private com.zemcho.guzhe.job.cron.WxDailyVisitTrendTask wxDailyVisitTrendTask;

    /**
     * 手动同步历史平台流量数据 (从微信拉取)
     * @param days 过去多少天 (默认7天)
     * @return
     */
    @RequestMapping(value = "/visit/trend/sync", method = {RequestMethod.GET, RequestMethod.POST})
    public Result syncVisitTrend(Integer days) {
        int d = (days != null && days > 0) ? days : 7;
        for (int i = 1; i <= d; i++) {
            java.time.LocalDate targetDate = java.time.LocalDateTime.now().minusDays(i).toLocalDate();
            wxDailyVisitTrendTask.syncDataForDate(targetDate);
        }
        return new Result();
    }

    /**
     * 平台用户统计
     *
     * @return
     */
    @RequestMapping(value = "/user/stat", method = {RequestMethod.GET, RequestMethod.POST})
    public Result userStat() {
        return service.userStat();
    }

    /**
     * 用户活跃度统计
     *
     * @return
     */
    @RequestMapping(value = "/active/stat", method = {RequestMethod.GET, RequestMethod.POST})
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
    @RequestMapping(value = "/visit/trend/stat", method = {RequestMethod.GET, RequestMethod.POST})
    public Result visitTrendStat(@Validated MonitorParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.visitTrendStat(param);
    }
    /**
     * 统计系统订单数据
     *
     * @return
     */
    @RequestMapping(value = "/order/get", method = {RequestMethod.GET, RequestMethod.POST})
    public Result getOrderData() {
        return service.getOrderData();
    }

    /**
     * 终端经营情况
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping(value = "/business/equipment", method = {RequestMethod.GET, RequestMethod.POST})
    public Result getBusinessEquipment(@Validated MonitorParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getBusinessEquipment(param);
    }

    /**
     * 导出终端经营情况数据
     *
     * @param param
     * @param response
     */
    @RequestMapping(value = "/export", method = {RequestMethod.GET, RequestMethod.POST})
    public void businessExport(MonitorParam param,
                               HttpServletResponse response) {
        System.out.println("=== 导出接口被调用 ===");
        System.out.println("param: " + param);
        try {
            service.businessExport(param, response);
        } catch (Exception e) {
            System.out.println("=== Controller 捕获异常 ===");
            System.out.println("异常类型: " + e.getClass().getName());
            System.out.println("异常信息: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
