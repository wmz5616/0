package com.zemcho.guzhe.controller.wechat.user;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteOneParam;
import com.zemcho.guzhe.controller.wechat.user.param.DeliveryAddressSaveParam;
import com.zemcho.guzhe.service.wechat.user.DeliveryAddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @title: DeliveryAddressController
 * @Description:
 * @Date: 2026/4/27 14:02
 */
@RestController
@RequestMapping("/wechat/delivery_address")
public class DeliveryAddressController {
    @Autowired
    private DeliveryAddressService service;

    /**
     * 添加收货地址
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/add")
    public Result add(@Valid @RequestBody DeliveryAddressSaveParam param, BindingResult result,
                      @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return service.add(param, token);
    }

    /**
     * 修改收货地址
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/update")
    public Result update(@Valid @RequestBody DeliveryAddressSaveParam param, BindingResult result,
                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return service.update(param, token);
    }

    /**
     * 获取收货地址列表
     *
     * @param token
     * @return
     */
    @RequestMapping("/list")
    public Result getList(@RequestHeader("token") String token) {
        return service.getList(token);
    }

    /**
     * 删除收货地址
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(@Valid @RequestBody DeleteOneParam param, BindingResult result,
                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return service.delete(param, token);
    }
}
