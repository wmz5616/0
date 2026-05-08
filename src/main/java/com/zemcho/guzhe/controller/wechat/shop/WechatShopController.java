package com.zemcho.guzhe.controller.wechat.shop;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.wechat.shop.param.WechatShopManagerParam;
import com.zemcho.guzhe.service.shop.ShopManagerService;
import com.zemcho.guzhe.service.shop.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @title: WechatShopController
 * @Description:
 * @Date: 2026/5/6 16:08
 */
@RestController
@RequestMapping("/wechat/shop")
public class WechatShopController {
    @Autowired
    private ShopService shopService;

    @Autowired
    private ShopManagerService shopManagerService;

    /**
     * 修改商家合同照片
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/contract")
    public Result updateContract(@Validated @RequestBody SearchParam param, BindingResult result,
                                 @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.updateContract(param, token, true);
    }

    /**
     * 获取商家管理者列表
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/manager/list")
    public Result shopManagerList(@Validated @RequestBody SearchParam param, BindingResult result,
                                  @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopManagerService.getByShopId(param, token, true);
    }

    /**
     * 新增商家管理者
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/manager/add")
    public Result addShopManager(@Validated @RequestBody WechatShopManagerParam param, BindingResult result,
                                 @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopManagerService.addShopManagerByWechat(param, token);
    }

    /**
     * 删除商家管理者
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/manager/del")
    public Result deleteShopManager(@Validated @RequestBody DeleteParam param, BindingResult result,
                                    @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopManagerService.deleteShopManager(param, token, true);
    }
}
