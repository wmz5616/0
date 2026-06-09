package com.zemcho.ddql.controller.wechat.personalCenter;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.controller.wechat.personalCenter.param.WechatMerchantCancelParam;
import com.zemcho.ddql.controller.wechat.personalCenter.param.WechatMerchantManageListParam;
import com.zemcho.ddql.service.wechat.personalCenter.WechatMerchantManageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 小程序商家管理接口
 */
@RestController
@RequestMapping("/wechat/personalCenter/merchant")
public class WechatMerchantManageController {
    @Autowired
    private WechatMerchantManageService wechatMerchantManageService;

    /**
     * 查询当前用户管理的商家列表
     *
     * @param param  分页参数
     * @param result 参数校验结果
     * @param token  小程序 token
     * @return 商家列表
     */
    @RequestMapping("/list")
    public Result list(@Valid @RequestBody WechatMerchantManageListParam param, BindingResult result,
                       @RequestHeader("token") String token) {
        // 先校验分页参数，避免无效请求进入业务层。
        if (result.hasErrors()) {
            return Result.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return wechatMerchantManageService.list(param, token);
    }

    /**
     * 注销商家
     *
     * @param param  商家ID
     * @param result 参数校验结果
     * @param token  小程序 token
     * @return 操作结果
     */
    @RequestMapping("/cancel")
    public Result cancel(@Valid @RequestBody WechatMerchantCancelParam param, BindingResult result,
                         @RequestHeader("token") String token) {
        // 注销前先校验商家ID参数
        if (result.hasErrors()) {
            return Result.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return wechatMerchantManageService.cancel(param, token);
    }
}
