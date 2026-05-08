package com.zemcho.guzhe.controller.wechat.shop;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.wechat.shop.param.ShopUpdateParam;
import com.zemcho.guzhe.service.wechat.shop.ShopAuditDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HXH
 */
//商家详情首页
@RestController
@RequestMapping("/wechat/shop/audit")
public class shopAuditDetailController {

    @Autowired
    private ShopAuditDetailService shopAuditDetailService;

    /**
     * 根据商家id获取商家经营数据
     * @param param searchId 商家ID
     * @param token token
     * @return result
     */
    @RequestMapping("/detail/businessData")
    public Result getBusinessData(@Validated @RequestBody SearchParam param, BindingResult result,
                                  @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopAuditDetailService.getBusinessData(param, token);
    }

    /**
     * 根据商家id获取商家信息
     * @param param searchId 商家ID
     * @param token token
     * @return result
     */
    @RequestMapping("/detail/get")
    public Result getShopInfo(@Validated @RequestBody SearchParam param, BindingResult result,
                                  @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopAuditDetailService.getShopInfo(param, token);
    }
    /**
     * 编辑商家信息
     * @param
     * @param token token
     * @return result
     */
    @RequestMapping("/detail/update")
    public Result update(@Validated @RequestBody ShopUpdateParam param, BindingResult result,
                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopAuditDetailService.update(param, token);
    }

}
