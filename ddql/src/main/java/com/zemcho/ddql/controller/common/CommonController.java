package com.zemcho.ddql.controller.common;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.dto.AuthAttrData;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.common.param.SmsCodeParam;
import com.zemcho.ddql.service.common.CommonService;
import com.zemcho.ddql.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @title: CommonController
 * @Description:
 * @Date: 2025/5/12 19:02
 */
@RestController
@RequestMapping("/common")
public class CommonController {
    @Autowired
    CommonService service;

    /**
     * 获取角色下拉列表
     *
     * @param param
     * @param result
     * @param authAttrData
     * @return
     */
    @RequestMapping("/role/lists")
    public Result getRoleLists(@Validated @RequestBody SearchParam param, BindingResult result,
                               @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData,
                               @RequestHeader String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getRoleLists(param, authAttrData, token);
    }

    /**
     * 获取管理员下拉列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/admin/lists")
    public Result getAdminLists(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getAdminLists(param);
    }

    /**
     * 发送短信验证码
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/sms/code")
    public Result sendSmsCode(@Validated @RequestBody SmsCodeParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.sendSmsCode(param);
    }

    /**
     * 获取图形验证码
     *
     * @return
     */
    @RequestMapping("/captcha/code")
    public Result getCaptchaCode(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getCaptchaCode();
    }

    /**
     * 获取用户下拉列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/user/lists")
    public Result getUserLists(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getUserLists(param);
    }

    /**
     * 获取商圈下拉列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/business/circle/lists")
    public Result getBusinessCircleLists(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getBusinessCircleLists(param);
    }

    /**
     * 获取店铺下拉列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/shop/lists")
    public Result getShopLists(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getShopLists(param);
    }

    /**
     * 获取快递公司下拉列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/express/company/lists")
    public Result getExpressCompanyLists(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getExpressCompanyLists(param);
    }

    /**
     * 获取快递公司下拉列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/merchant/lists")
    public Result getMerchantLists(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return null;
    }
}
