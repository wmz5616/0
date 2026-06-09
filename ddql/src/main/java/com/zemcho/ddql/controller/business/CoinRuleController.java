package com.zemcho.ddql.controller.business;

import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.param.CoinRuleParam;
import com.zemcho.ddql.service.business.CoinRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用币规则控制器
 */
@RestController
@RequestMapping("/business/shop/coin")
public class CoinRuleController {

    @Autowired
    private CoinRuleService coinRuleService;

    /**
     * 新增用币规则
     *
     * @param param  参数对象
     * @param result 验证结果
     * @return 结果
     */
    @Log(description = "新增用币规则", module = "店铺管理-新增用币规则")
    @RequestMapping("/add")
    public Result addCoinRule(@Validated @RequestBody CoinRuleParam param,
                              BindingResult result,
                              @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return coinRuleService.addCoinRule(param,token,false);
    }

    /**
     * 更新用币规则
     *
     * @param param  参数对象
     * @param result 验证结果
     * @return 结果
     */
    @Log(description = "更新用币规则", module = "店铺管理-更新用币规则")
    @RequestMapping("/update")
    public Result updateCoinRule(@Validated @RequestBody CoinRuleParam param,
                                 BindingResult result,
                                 @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return coinRuleService.updateCoinRule(param,token,false);
    }


    /**
     * 根据商家ID查询用币规则列表
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
        return coinRuleService.getByShopId(param,token,false);
    }

}