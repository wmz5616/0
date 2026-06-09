package com.zemcho.guzhe.controller.wechat.screen;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.wechat.screen.param.ScreenRentalAvailableParam;
import com.zemcho.guzhe.controller.wechat.screen.param.ScreenRentalRentParam;
import com.zemcho.guzhe.service.wechat.screen.ScreenRentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 屏幕店位租用
 */
@RestController
@RequestMapping("/wechat/screen_rental")
public class ScreenRentalController {
    @Autowired
    private ScreenRentalService service;

    /**
     * 获取可租用店位列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/available/lists")
    public Result availableLists(@Validated @RequestBody ScreenRentalAvailableParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return service.availableLists(param);
    }

    /**
     * 屏幕店位租用
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    //@Log(description = "租用下单", module = "屏幕店位租用")
    @RequestMapping("/rent")
    public Result rent(@Validated @RequestBody ScreenRentalRentParam param, BindingResult result,
                       @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return service.rent(param, token);
    }
}
