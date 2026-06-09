package com.zemcho.ddql.controller.audit;


import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.audit.param.ShopAuditHandleParam;
import com.zemcho.ddql.controller.business.param.BusinessCircleParam;
import com.zemcho.ddql.service.wechat.shop.AuditShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 后台审核信息管理
 */
@RestController
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    private AuditShopService auditShopService;

    /**
     * 查询提交审核列表
     * @param param searchField1 提交用户ID searchId 审核人Id keyword 商家名称
     *              startTime 提交开始时间 endTime 提交结束时间 searchField2 审核开始时间 searchField3 审核结束时间
     * @return result
     */
    @Log(description = "查询提交审核列表", module = "审核管理-查询提交审核列表")
    @RequestMapping("/list")
    public Result getAuditShopList(@Validated @RequestBody SearchParam param, BindingResult result,
                                   @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return auditShopService.getAuditShopList(param, token);
    }

    /**
     * 处理审核
     * @param param keyword 驳回原因 searchType 审核结果 1通过 2 驳回 searchId 提交的信息id
     * @param token token
     * @return result
     */
    @Log(description = "处理审核", module = "审核管理-处理审核")
    @RequestMapping("/handle")
    public Result handleAuditShop(@Validated @RequestBody ShopAuditHandleParam param, BindingResult result,
                                  @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return auditShopService.handleAuditShop(param, token);
    }

    /**
     * 查询还未审核通过的商家信息
     * @param param searchId 审核记录ID
     * @param token token
     * @return result
     */
    @RequestMapping("/get")
    public Result getUnAuditShop(@Validated @RequestBody SearchParam param, BindingResult result,
                                  @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return auditShopService.getUnAuditShop(param, token);
    }

}
