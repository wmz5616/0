package com.zemcho.guzhe.controller.shop;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.wechat.shop.param.ShopAuditHandleParam;
import com.zemcho.guzhe.service.shop.ShopAuditService;
import com.zemcho.guzhe.service.shop.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author HXH
 */
//后台商家管理
@RestController
@RequestMapping("/shop/audit")
public class ShopAuditController {

    @Autowired
    private ShopAuditService shopAuditService;
    /**
     * 获取后台商家入驻审核列表
     */
    @RequestMapping("/lists")
    public Result selectList(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopAuditService.selectList(param);
    }

    /**
     * 获取商家入驻审核详情
     */
    @RequestMapping("/detail")
    public Result detail(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopAuditService.detail(param);
    }

    /**
     * 后台管理员编辑待审核商家信息
     */
    @RequestMapping("/handle")
    public Result handle(@Validated @RequestBody SearchParam param,
                         BindingResult result,
                         @RequestHeader("token") String token,
                         @RequestBody ShopAuditHandleParam shopAuditHandleParam
    ) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopAuditService.handle(param,token,shopAuditHandleParam);
    }

    /**
     * 根据商家id获取商家资质认证信息
     */
    @RequestMapping("/get")
    public Result get(@Validated @RequestBody SearchParam param,
                         BindingResult result,
                         @RequestHeader("token") String token
    ) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopAuditService.get(param,token);
    }

    /**
     * 审核资质认证
     *
     * @param param  审核参数
     * @param result 验证结果
     * @return 结果
     */
    @Log(description = "审核资质认证", module = "商家管理")
    @RequestMapping("/qualificationCert")
    public Result auditQualificationCert(@Validated @RequestBody SearchParam param, BindingResult result,
                                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopAuditService.auditQualificationCert(param, token);
    }
}
