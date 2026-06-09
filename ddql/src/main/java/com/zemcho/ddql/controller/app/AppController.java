package com.zemcho.ddql.controller.app;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.app.param.AppAuthParam;
import com.zemcho.ddql.controller.app.param.AppVersionUpdateParam;
import com.zemcho.ddql.service.app.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class AppController {
    @Autowired
    AppService service;

    /**
     * app端获取AccessToken
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/accessToken")
    public Result getAccessToken(@Validated @RequestBody AppAuthParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getAccessToken(param);
    }

    /**
     * 获取设备海报列表
     *
     * @param accessToken
     * @return
     */
    @RequestMapping("/poster/lists")
    public Result getPosterLists(@RequestHeader("accessToken") String accessToken) {
        return service.getPosterLists(accessToken);
    }

    /**
     * 获取设备详情
     *
     * @param accessToken
     * @return
     */
    @RequestMapping("/equipment/info")
    public Result getEquipmentInfo(@RequestHeader("accessToken") String accessToken) {
        return service.getEquipmentInfo(accessToken);
    }

    /**
     * 获取最新版本信息
     *
     * @param accessToken
     * @return
     */
    @RequestMapping("/version/latest/info")
    public Result latestVersionInfo(@RequestHeader("accessToken") String accessToken) {
        return service.latestVersionInfo(accessToken);
    }

    /**
     * 更新设备版本信息
     *
     * @param param
     * @param result
     * @param accessToken
     * @return
     */
    @RequestMapping("/version/update")
    public Result updateVersion(@Validated @RequestBody AppVersionUpdateParam param,
                                BindingResult result,
                                @RequestHeader("accessToken") String accessToken) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.updateVersion(param, accessToken);
    }

    /**
     * 获取签到配置信息
     *
     * @param accessToken
     * @return
     */
    @RequestMapping("/check_in/config")
    public Result getCheckInConfig(@RequestHeader("accessToken") String accessToken) {
        return service.getCheckInConfig(accessToken);
    }

    /**
     * 获取打卡店铺列表
     *
     * @param accessToken
     * @return
     */
    @RequestMapping("/shop/lists")
    public Result getShopLists(@Validated @RequestBody SearchParam param, BindingResult result,
                               @RequestHeader("accessToken") String accessToken) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getShopLists(param, accessToken);
    }

    /**
     * 获取用户打卡信息
     *
     * @param accessToken
     * @return
     */
    @RequestMapping("/user/check_in/info")
    public Result getUserCheckInInfo(@RequestHeader("accessToken") String accessToken) {
        return service.getUserCheckInInfo(accessToken);
    }
}
