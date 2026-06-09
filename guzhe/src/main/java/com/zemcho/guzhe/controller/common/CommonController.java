package com.zemcho.guzhe.controller.common;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthAttrData;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.common.param.SmsCodeParam;
import com.zemcho.guzhe.controller.wechat.common.param.WechatBankQueryParam;
import com.zemcho.guzhe.service.common.CommonService;
import com.zemcho.guzhe.util.Constant;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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
     * 根据当前地区id查询下级地区
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/region/low/list")
    public Result getLowRegionList(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return service.getLowRegionList(param);
    }

    /**
     * 根据地区id查询上级地区
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/region/parent/info")
    public Result getRegionParentInfo(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return service.getRegionParentInfo(param);
    }

    /**
     * 根据地区id查询地区信息
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/region/info")
    public Result getRegionInfo(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return service.getRegionInfo(param);
    }

    /**
     * 获取行业分类下拉列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/industry_category/lists")
    public Result getIndustryCategoryLists(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getIndustryCategoryLists(param);
    }

    /**
     * 获取商超下拉列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/supermarket/lists")
    public Result getSupermarketLists(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getSupermarketLists(param);
    }

    /**
     * 获取商家下拉列表
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
     * 根据bank查询去重后的银行编码和银行名称
     */
    @RequestMapping("/bank/lists")
    public Result getBankLists(@Validated @RequestBody WechatBankQueryParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return service.getWechatBankLists(param);
    }

    /**
     * 根据bank表条件查询省市编码和名称
     */
    @RequestMapping("/bank/province/city/lists")
    public Result getBankProvinceCityLists(@Validated @RequestBody WechatBankQueryParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return service.getWechatBankProvinceCityLists(param);
    }

    /**
     * 根据bank表条件查询联行号和联行名称
     */
    @RequestMapping("/bank/link/lists")
    public Result getBankLinkLists(@Validated @RequestBody WechatBankQueryParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return service.getWechatBankLinkLists(param);
    }

}
