package com.zemcho.guzhe.controller.shop;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.shop.param.ShopManagerParam;
import com.zemcho.guzhe.service.shop.ShopManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店铺管理者控制器
 */
@RestController
@RequestMapping("/shop/manager")
public class ShopManagerController {

    @Autowired
    private ShopManagerService shopManagerService;

    /**
     * 新增店铺管理者
     *
     * @param param  参数对象
     * @param result 验证结果
     * @return 结果
     */
    @Log(description = "新增店铺管理者", module = "商家管理")
    @RequestMapping("/add")
    public Result addShopManager(@Validated @RequestBody ShopManagerParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopManagerService.addShopManager(param);
    }

    /**
     * 更新店铺管理者
     *
     * @param param  参数对象
     * @param result 验证结果
     * @return 结果
     */
    @Log(description = "更新店铺管理者", module = "商家管理")
    @RequestMapping("/update")
    public Result updateShopManager(@Validated @RequestBody ShopManagerParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopManagerService.updateShopManager(param);
    }

    /**
     * 删除店铺管理者
     *
     * @param param  删除参数
     * @param result 验证结果
     * @return 结果
     */
    @Log(description = "删除店铺管理者", module = "商家管理")
    @RequestMapping("/del")
    public Result deleteShopManager(@Validated @RequestBody DeleteParam param, BindingResult result,
                                    @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopManagerService.deleteShopManager(param, token, false);
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
        return shopManagerService.getByShopId(param, token, false);
    }
}