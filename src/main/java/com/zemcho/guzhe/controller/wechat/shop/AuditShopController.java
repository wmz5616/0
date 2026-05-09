package com.zemcho.guzhe.controller.wechat.shop;


import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.shop.param.QualificationCertParam;
import com.zemcho.guzhe.controller.wechat.shop.param.AuditShopParam;
import com.zemcho.guzhe.service.shop.IndustryCategoryService;
import com.zemcho.guzhe.service.supermarket.SupermarketService;
import com.zemcho.guzhe.service.wechat.shop.AuditShopService;
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
    private IndustryCategoryService industryCategoryService;
    @Autowired
    private SupermarketService supermarketService;
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
     * 小程序查询申请入驻记录
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
     * @return {
     *   "isMerchantAdmin": true/false,
     *   "merchantList": [...],  // 如果是管理员，返回商家列表
     *   "refundPendingCount": 2 // 退款审核订单数量
     * }
     */
    @RequestMapping("/check/merchant/admin")
    public Result checkMerchantAdmin(@RequestHeader("token") String token) {
        return auditShopService.checkMerchantAdmin(token);
    }

    /**
     * 获取行业类别
     *
     * @return result
     */
    @RequestMapping("/industry/get")
    public Result getIndustryCateList() {
        return industryCategoryService.getList();
    }
    /**
     * 获取已启用商超信息
     *
     * @return result
     */
    @RequestMapping("/super/get")
    public Result get() {
        return supermarketService.get();
    }
}
