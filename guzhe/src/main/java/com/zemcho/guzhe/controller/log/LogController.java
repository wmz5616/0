package com.zemcho.guzhe.controller.log;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthAttrData;
import com.zemcho.guzhe.controller.log.param.LogParam;
import com.zemcho.guzhe.service.log.LogService;
import com.zemcho.guzhe.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @title: LogController
 * @Description:
 * @Date: 2024/1/24 10:07
 */
@RestController
@RequestMapping("/log")
public class LogController {
    @Autowired
    LogService logService;

    /**
     * 获取登录日志列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/login/lists")
    public Result getLoginLogList(@Validated @RequestBody LogParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return logService.getLoginLogList(param);
    }

    /**
     * 获取操作日志列表
     *
     * @param param
     * @param result
     * @param authAttrData
     * @return
     */
    @RequestMapping("/operate/lists")
    public Result getOperateLogList(@Validated @RequestBody LogParam param, BindingResult result,
                                    @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return logService.getOperateLogList(param, authAttrData);
    }
}
