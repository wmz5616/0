package com.zemcho.guzhe.controller.shop;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.ChangeOneParam;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.shop.param.ShopParam;
import com.zemcho.guzhe.controller.shop.param.ShopPosterSaveParam;
import com.zemcho.guzhe.service.shop.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 新增/编辑店铺
     *
     * @return
     */
    @Log(description = "新增/编辑商家", module = "商家管理")
    @RequestMapping("/save")
    public Result saveShop(@Validated @RequestBody ShopParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.saveShop(param);
    }

    /**
     * 删除店铺
     *
     * @param param
     * @return
     */
    @Log(description = "删除商家", module = "商家管理")
    @RequestMapping("/delete")
    public Result deleteShop(@Validated @RequestBody DeleteParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.deleteShop(param);
    }

    /**
     * 获取店铺列表
     */
    @RequestMapping("/lists")
    public Result selectList(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.selectList(param);
    }

    /**
     * 禁用/启用店铺
     */
    @Log(description = "禁用/启用商家", module = "商家管理")
    @RequestMapping("/status")
    public Result updateStatus(@Validated @RequestBody ChangeOneParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.updateStatus(param);
    }

    /**
     * 注销商家
     *
     * @param param
     * @param result
     * @return
     */
    @Log(description = "注销商家", module = "商家管理")
    @RequestMapping("/off")
    public Result shopOff(@Validated @RequestBody ChangeOneParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        param.setStatus(2);
        return shopService.updateStatus(param);
    }

    /**
     * 禁用/启用商家消费置顶
     */
    @Log(description = "禁用/启用消费置顶", module = "商家管理")
    @RequestMapping("/topConsumption/status")
    public Result updateTopConsumptionStatus(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.updateTopConsumptionStatus(param);
    }

    /**
     * 修改商家收款配置
     */
    @Log(description = "修改商家收款", module = "商家管理")
    @RequestMapping("/receipt/status")
    public Result updateReceiptConfig(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.updateReceiptConfig(param);
    }

    /**
     * 修改商家合同照片
     */
    @Log(description = "修改商家合同照片", module = "商家管理")
    @RequestMapping("/contract")
    public Result updateContract(@Validated @RequestBody SearchParam param, BindingResult result,
                                 @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.updateContract(param, token, false);
    }


    /**
     * 根据id查询店铺详情
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/selectById")
    public Result selectById(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.selectById(param);
    }

    /**
     * 获取商家海报列表
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/poster/lists")
    public Result getShopPosterLists(@Validated @RequestBody SearchParam param, BindingResult result,
                                     @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.getShopPosterLists(param, token, false);
    }

    /**
     * 保存海报
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @Log(description = "保存海报", module = "商家管理")
    @RequestMapping("/poster/save")
    public Result saveShopPoster(@Validated @RequestBody ShopPosterSaveParam param, BindingResult result,
                                 @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.saveShopPoster(param, token, false);
    }
}
