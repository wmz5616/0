package com.zemcho.guzhe.controller.wechat.screenOrder;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderDisplayUpdateParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderInfoParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderListParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderScreenshotQueryParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderScreenshotTaskParam;
import com.zemcho.guzhe.service.wechat.screenOrder.ScreenOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店位订单
 */
@RestController
@RequestMapping("/wechat/screen_order")
public class ScreenOrderController {
    @Autowired
    private ScreenOrderService service;

    /**
     * 获取店位订单列表
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/lists")
    public Result lists(@Validated @RequestBody ScreenOrderListParam param, BindingResult result,
                        @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.lists(param, token);
    }

    /**
     * 获取店位订单详情
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/info")
    public Result info(@Validated @RequestBody ScreenOrderInfoParam param, BindingResult result,
                       @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.info(param, token);
    }

    /**
     * 修改店位订单展示内容
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/display/update")
    public Result updateDisplayType(@Validated @RequestBody ScreenOrderDisplayUpdateParam param, BindingResult result,
                                    @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.updateDisplayType(param, token);
    }

    /**
     * 获取是否展示屏幕店租用合约
     *
     * @return
     */
    @RequestMapping("/contract/status")
    public Result contractStatus() {
        return service.contractStatus();
    }

    /**
     * 获取屏幕店租用合约内容
     *
     * @return
     */
    @RequestMapping("/contract/content")
    public Result contractContent() {
        return service.contractContent();
    }

    /**
     * 发起设备截图任务
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/screenshot/task")
    public Result createScreenshotTask(@Validated @RequestBody ScreenOrderScreenshotTaskParam param, BindingResult result,
                                       @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.createScreenshotTask(param, token);
    }

    /**
     * 查询设备截图结果
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/screenshot/info")
    public Result screenshotInfo(@Validated @RequestBody ScreenOrderScreenshotQueryParam param, BindingResult result,
                                 @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.screenshotInfo(param, token);
    }
}
