package com.zemcho.guzhe.controller.app;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.app.param.AppAuthParam;
import com.zemcho.guzhe.controller.app.param.AppVersionUpdateParam;
import com.zemcho.guzhe.controller.app.param.ProductOrderQRParam;
import com.zemcho.guzhe.service.app.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class AppController {
    @Autowired
    AppService service;


    /**
     * app端获取AccessToken
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/accessToken")
    public Result getAccessToken(@Validated @RequestBody AppAuthParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getAccessToken(param);
    }

    /**
     * 获取设备详情
     *
     * @param accessToken
     * @return
     */
    @RequestMapping("/equipment/info")
    public Result getEquipmentInfo(@RequestHeader("accessToken") String accessToken) {
        return service.getEquipmentInfo(accessToken);
    }

    /**
     * 获取最新版本信息
     *
     * @param accessToken
     * @return
     */
    @RequestMapping("/version/latest/info")
    public Result latestVersionInfo(@RequestHeader("accessToken") String accessToken) {
        return service.latestVersionInfo(accessToken);
    }

    /**
     * 更新设备版本信息
     *
     * @param param
     * @param result
     * @param accessToken
     * @return
     */
    @RequestMapping("/version/update")
    public Result updateVersion(@Validated @RequestBody AppVersionUpdateParam param, BindingResult result,
                                @RequestHeader("accessToken") String accessToken) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.updateVersion(param, accessToken);
    }

    /**
     * 获取设备下的商家列表
     *
     * @param accessToken
     * @return
     */
    @RequestMapping("/shop/list")
    public Result getShopList(@RequestHeader("accessToken") String accessToken) {
        return service.getShopList(accessToken);
    }

    /**
     * 获取设备下某商家的商品分类列表
     *
     * @param param
     * @param result
     * @param accessToken
     * @return
     */
    @RequestMapping("/product/category/list")
    public Result getProductCategoryList(@Validated @RequestBody SearchParam param, BindingResult result,
                                         @RequestHeader("accessToken") String accessToken) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getProductCategoryList(param, accessToken);
    }

    /**
     * 获取设备下某商家的商品列表
     *
     * @param param
     * @param result
     * @param accessToken
     * @return
     */
    @RequestMapping("/product/list")
    public Result getProductList(@Validated @RequestBody SearchParam param, BindingResult result,
                                 @RequestHeader("accessToken") String accessToken) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getProductList(param, accessToken);
    }

    /**
     * 获取商品详情
     *
     * @param param
     * @param result
     * @param accessToken
     * @return
     */
    @RequestMapping("/product/info")
    public Result getProductInfo(@Validated @RequestBody SearchParam param, BindingResult result,
                                 @RequestHeader("accessToken") String accessToken) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getProductInfo(param, accessToken);
    }

    /**
     * 获取商品订单小程序码
     *
     * @param param
     * @param result
     * @param accessToken
     * @return
     */
    @RequestMapping("/product/order/qr_code")
    public Result getProductOrderQRCode(@Validated @RequestBody ProductOrderQRParam param, BindingResult result,
                                        @RequestHeader("accessToken") String accessToken) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getProductOrderQRCode(param, accessToken);
    }

    /**
     * 获取设备屏幕店位租赁订单小程序码
     *
     * @param accessToken
     * @return
     */
    @RequestMapping("/screen/order/qr_code")
    public Result getScreenOrderQRCode(@RequestHeader("accessToken") String accessToken) {
        return service.getScreenOrderQRCode(accessToken);
    }

    /**
     * 获取设备下的商品订单详情
     *
     * @param accessToken
     * @return
     */
    @RequestMapping("/product/order/info")
    public Result getProductOrderInfo(@RequestHeader("accessToken") String accessToken) {
        return service.getProductOrderInfo(accessToken);
    }
}
