package com.zemcho.ddql.controller.wechat.shop;


import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.wechat.shop.param.WechatShopDetailParam;
import com.zemcho.ddql.service.wechat.shop.ConsumptionShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 小程序消费门店
 */
@RestController
@RequestMapping("/wechat/consumption")
public class ConsumptionShopController {

    @Autowired
    private ConsumptionShopService consumptionShopService;

    /**
     * 获取商家列表
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/list")
    public Result selectList(@Validated @RequestBody SearchParam param, BindingResult result,
                             @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return consumptionShopService.selectList(param, token);
    }

    /**
     * 获取商家详情（包含用币规则）
     *
     * @param param  请求参数
     * @param result 校验结果
     * @return 商家详情
     */
    @RequestMapping("/get")
    public Result getShopDetail(@Validated @RequestBody WechatShopDetailParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return consumptionShopService.getShopDetail(param);
    }

}
