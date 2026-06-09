package com.zemcho.ddql.controller.wechat.shop;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.wechat.shop.param.WechatShopParam;
import com.zemcho.ddql.service.business.BusinessCircleService;
import com.zemcho.ddql.service.wechat.shop.WechatShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wechat")
public class WechatShopController {
    @Autowired
    private WechatShopService wechatShopService;

    @Autowired
    private BusinessCircleService businessCircleService;

    /**
     * 获取打卡店铺列表
     */
    @RequestMapping("/shop/lists")
    public Result selectList(@Validated @RequestBody WechatShopParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return wechatShopService.selectList(param);
    }

    /**
     * 获取商圈信息
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/circle/info")
    public Result getCircleInfo(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return businessCircleService.selectById(param);
    }

    /**
     * 获取商圈下的店铺列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/circle/shop/list")
    public Result getCircleShopList(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return wechatShopService.getCircleShopList(param);
    }

    /**
     * 店铺点击次数+1
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/shop/click_count/inc")
    public Result shopClickCountInc(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return wechatShopService.shopClickCountInc(param);
    }

    /**
     * 修改商家合同照片
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/shop/contract")
    public Result updateContract(@Validated @RequestBody SearchParam param, BindingResult result,
                                 @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return wechatShopService.updateContract(param, token);
    }


    /**
     * 根据商家id获取商家经营数据
     * @param param searchId 商家ID
     * @param token token
     * @return result
     */
    @RequestMapping("/shop/detail/businessData")
    public Result getBusinessData(@Validated @RequestBody SearchParam param, BindingResult result,
                                  @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return wechatShopService.getBusinessData(param, token);
    }
}
