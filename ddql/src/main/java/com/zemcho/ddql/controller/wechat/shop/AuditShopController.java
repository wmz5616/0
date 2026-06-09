package com.zemcho.ddql.controller.wechat.shop;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.param.QualificationCertParam;
import com.zemcho.ddql.controller.wechat.shop.param.AuditShopParam;
import com.zemcho.ddql.service.business.ShopService;
import com.zemcho.ddql.service.wechat.shop.AuditShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wechat/shop/audit")
public class AuditShopController {

    @Autowired
    private AuditShopService auditShopService;

    @Autowired
    private ShopService shopService;


    /**
     * 提交商家信息审核
     *
     * @param auditShopParam 商家信息
     * @param result         参数校验结果
     * @param token          token
     * @return result
     */
    @RequestMapping("/submit")
    public Result submitAuditShop(@Validated @RequestBody AuditShopParam auditShopParam, BindingResult result,
                                  @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return auditShopService.submitAuditShop(auditShopParam, token);
    }

    /**
     * 小程序提交资质认证
     *
     * @param qualificationCertParam 资质认证信息
     * @param result                 result
     * @param token                  token
     * @return result
     */
    @RequestMapping("/qualification/submit")
    public Result submitQualification(@Validated @RequestBody QualificationCertParam qualificationCertParam, BindingResult result,
                                      @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return auditShopService.submitQualification(qualificationCertParam, token);
    }

    /**
     * 小程序查询资质认证
     *
     * @param param 查询条件
     * @param result                 result
     * @param token                  token
     * @return result
     */
    @RequestMapping("/qualification/get")
    public Result getQualification(@Validated @RequestBody SearchParam param, BindingResult result,
                                      @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return auditShopService.getQualification(param, token);
    }

    /**
     * 小程序查询个人申请入驻记录
     *
     * @param param  param
     * @param result result
     * @param token  token
     * @return result
     */
    @RequestMapping("/list")
    public Result getAuditShopList(@Validated @RequestBody SearchParam param, BindingResult result,
                                   @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return auditShopService.getOwnApplyList(param, token);
    }

    /**
     * 小程序根据id查询申请入驻记录
     *
     * @param param searchId 申请入驻记录ID
     * @param token token
     * @return result
     */
    @RequestMapping("/get")
    public Result getAuditShopById(@Validated @RequestBody SearchParam param, BindingResult result,
                                   @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return auditShopService.getAuditShopById(token, param);
    }

    /**
     * 检查用户是否为商家管理员，并返回对应页面数据
     *
     * @param token token
     * }
     */
    @RequestMapping("/check/merchant/admin")
    public Result checkMerchantAdmin(@RequestHeader("token") String token) {
        return auditShopService.checkMerchantAdmin(token);
    }

    /**
     * 小程序根据商家id查询商家详情信息
     *
     * @param param searchId 商家ID
     * @param token token
     * @return result
     */
    @RequestMapping("/getDetail")
    public Result getDetail(@Validated @RequestBody SearchParam param, BindingResult result,
                                   @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.selectById(param,true,token);
    }

}
