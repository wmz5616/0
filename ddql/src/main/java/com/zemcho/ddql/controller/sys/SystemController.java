package com.zemcho.ddql.controller.sys;

import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.controller.sys.param.ConfigParam;
import com.zemcho.ddql.service.sys.SystemService;
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
}