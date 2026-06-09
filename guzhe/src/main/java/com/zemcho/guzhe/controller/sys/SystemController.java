package com.zemcho.guzhe.controller.sys;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.sys.param.ConfigParam;
import com.zemcho.guzhe.service.sys.SystemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Ryan
 */
@RestController
@RequestMapping("/system")
public class SystemController {
    @Autowired
    SystemService systemService;

    /**
     * 获取系统基础配置信息
     *
     * @return
     */
    @RequestMapping("/basic/config")
    public Result getBasicConfig() {
        return systemService.getBasicConfig();
    }

    /**
     * 获取屏幕店租用合约配置
     *
     * @return
     */
    @RequestMapping("/basic/config/rental")
    public Result getRentalContractConfig() {
        return systemService.getRentalContractConfig();
    }
    /**
     * 修改基础配置信息
     *
     * @return
     */
    @Log(description = "修改基础配置信息", module = "基础配置")
    @RequestMapping("/basic/config/update")
    public Result updateBasicConfig(@Valid @RequestBody ConfigParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return systemService.updateBasicConfig(param);
    }

    /**
     * 修改屏幕店租用合约配置
     *
     * @return
     */
    @Log(description = "修改屏幕店租用合约配置", module = "基础配置")
    @RequestMapping("/basic/config/rental/update")
    public Result updateRentalContractConfig(@Valid @RequestBody ConfigParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return systemService.updateRentalContractConfig(param);
    }

    /**
     * 获取登录页配图
     *
     * @return
     */
    @RequestMapping("/login/pic")
    @Log(description = "获取登录页配图", module = "系统配置")
    public Result getLoginPic() {
        return systemService.getLoginPic();
    }

    /**
     * 保存登录页配图
     *
     * @return
     */
    @Log(description = "保存登录页配图", module = "系统配置")
    @RequestMapping("/login/pic/save")
    public Result saveLoginPic(@Valid @RequestBody ConfigParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return systemService.saveLoginPicConfig(param);
    }

    /**
     * 获取入驻前须知配置
     *
     * @return
     */
    @RequestMapping("/basic/config/merchant-notice")
    public Result getMerchantNoticeConfig() {
        return systemService.getMerchantNoticeConfig();
    }

    /**
     * 修改入驻前须知配置
     *
     * @return
     */
    @Log(description = "修改入驻前须知配置", module = "基础配置")
    @RequestMapping("/basic/config/merchant-notice/update")
    public Result updateMerchantNoticeConfig(@Valid @RequestBody ConfigParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return systemService.updateMerchantNoticeConfig(param);
    }


}