package com.zemcho.ddql.controller.wechat.shop;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.param.ShopManagerParam;
import com.zemcho.ddql.service.business.ShopManagerService;
import com.zemcho.ddql.service.wechat.shop.RuleService;
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
//商家权限管理
@RestController
@RequestMapping("/wechat/shop/rule")
public class RuleController {
    @Autowired
    private RuleService ruleService;
    @Autowired
    private ShopManagerService shopManagerService;

    /**
     * 注销商家
     *
     * @param result         参数校验结果
     * @param token          token
     * @return result
     */
    @RequestMapping("/shopStatus")
    public Result updateShopStatus(@Validated @RequestBody SearchParam param, BindingResult result,
                                  @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return ruleService.updateShopStatus(param, token);
    }

    /**
     * 根据电话查询商家管理人员
     *
     * @param result         参数校验结果
     * @param token          token
     * @return result
     */
    @RequestMapping("/select")
    public Result selectByPhone(@Validated @RequestBody SearchParam param, BindingResult result,
                                   @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return ruleService.selectByPhone(param, token);
    }

    /**
     * 设置管理员为店长
     *
     * @param result         参数校验结果
     * @param token          token
     * @return result
     */
    @RequestMapping("/update")
    public Result update(@Validated @RequestBody SearchParam param, BindingResult result,
                                @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return ruleService.update(param, token);
    }

    /**
     * 删除管理员
     *
     * @param result         参数校验结果
     * @param token          token
     * @return result
     */
    @RequestMapping("/delete")
    public Result delete(@Validated @RequestBody DeleteParam param, BindingResult result,
                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopManagerService.deleteShopManager(param, token,true);
    }

    /**
     * 添加管理员
     *
     * @param result         参数校验结果
     * @param token          token
     * @return result
     */
    @RequestMapping("/add")
    public Result add(@Validated @RequestBody ShopManagerParam param, BindingResult result,
                      @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopManagerService.addShopManager(param,token,true);
    }
    /**
     * 根据商家ID查询店铺管理者列表
     *
     * @param param  查询参数
     * @param result 验证结果
     * @return 结果
     */
    @RequestMapping("/get")
    public Result getByShopId(@Validated @RequestBody SearchParam param, BindingResult result,
                              @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopManagerService.getByShopId(param,token,true);
    }

}
