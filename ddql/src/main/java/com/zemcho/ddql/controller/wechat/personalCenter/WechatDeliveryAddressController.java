package com.zemcho.ddql.controller.wechat.personalCenter;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.entity.personalCenter.DeliveryAddress;
import com.zemcho.ddql.service.personalCenter.DeliveryAddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 收货地址
 */
@RestController
@RequestMapping("/wechat/personalCenter/deliveryAddress")
public class WechatDeliveryAddressController {

    @Autowired
    private DeliveryAddressService deliveryAddressService;


    /**
     * 添加收货地址
     *
     * @param data
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/add")
    public Result add(@Valid @RequestBody DeliveryAddress data, BindingResult result,
                      @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return deliveryAddressService.add(data, token);
    }

    /**
     * 修改收货地址
     *
     * @param data
     * @param result
     * @return
     */
    @RequestMapping("/update")
    public Result update(@Valid @RequestBody DeliveryAddress data, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return deliveryAddressService.update(data);
    }

    /**
     * 查询收货地址
     *
     * @param data
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/select")
    public Result select(@Valid @RequestBody DeliveryAddress data, BindingResult result,
                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return deliveryAddressService.select(token);
    }

    /**
     * 删除收货地址
     *
     * @param data
     * @param result
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(@Valid @RequestBody DeliveryAddress data, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return deliveryAddressService.delete(data.getId());
    }

}
