package com.zemcho.ddql.controller.wechat.index;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.cas.param.UserCoinLogParam;
import com.zemcho.ddql.controller.product.param.ProductSearchParam;
import com.zemcho.ddql.controller.wechat.index.param.*;
import com.zemcho.ddql.util.tgy.dto.WxJsPayCallBackResponse;
import com.zemcho.ddql.service.product.CategoryService;
import com.zemcho.ddql.service.product.ProductService;
import com.zemcho.ddql.service.wechat.index.WechatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestController
@RequestMapping("/wechat")
public class WechatController {
    @Autowired
    WechatService service;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductService productService;

    /**
     * 获取用户今日运动信息
     *
     * @param token
     * @return
     */
    @RequestMapping("/user/today/sport/info")
    public Result userTodaySportInfo(@RequestHeader("token") String token) {
        return service.getUserTodaySportInfo(token);
    }

    /**
     * 更新用户今日步数
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/user/today/step_num/set")
    public Result userTodayStepNumUpdate(@Validated @RequestBody StepNumUpdateParam param,
                                         BindingResult result,
                                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.userTodayStepNumUpdate(param, token);
    }

    /**
     * 获取打卡场地列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/place/lists")
    public Result placeLists(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getPlaceLists(param);
    }

    /**
     * 获取打卡场地信息
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/place/info")
    public Result placeInfo(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getPlaceInfo(param);
    }

    /**
     * 获取设备信息
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/equipment/info")
    public Result equipmentInfo(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getEquipmentInfo(param);
    }

    /**
     * 获取打卡配置信息
     *
     * @return
     */
    @RequestMapping("/check_in/setting")
    public Result checkInSetting() {
        return service.getCheckInSetting();
    }

    /**
     * 用户打卡
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/user/check_in")
    public Result userCheckIn(@Validated @RequestBody CheckInParam param, BindingResult result,
                              @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.userCheckIn(param, token);
    }

    /**
     * 获取用户打卡中的数据
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/user/check_in/running/info")
    public Result userCheckInRunningInfo(@Validated @RequestBody SearchParam param, BindingResult result,
                                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.userCheckInRunningInfo(param, token);
    }

    /**
     * 取消用户打卡
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/user/check_in/cancel")
    public Result userCheckInCancel(@Validated @RequestBody SearchParam param, BindingResult result,
                                    @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.userCheckInCancel(param, token);
    }

    /**
     * 获取用户打卡详情
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/user/check_in/info")
    public Result userCheckInInfo(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.userCheckInInfo(param);
    }

    /**
     * 获取用户打卡列表
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/user/check_in/list")
    public Result userCheckInList(@Validated @RequestBody SearchParam param, BindingResult result,
                                  @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.userCheckInList(param, token);
    }

    /**
     * 获取用户打卡统计信息
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/user/check_in/count")
    public Result userCheckInCount(@Validated @RequestBody SearchParam param, BindingResult result,
                                   @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.userCheckInCount(param, token);
    }

    /**
     * 用户打卡数据导出到邮箱
     *
     * @param param
     * @param result
     * @param token
     * @param request
     * @return
     */
    @RequestMapping("/user/check_in/export")
    public Result userCheckInExportToEmail(@Validated @RequestBody SearchParam param, BindingResult result,
                                           @RequestHeader("token") String token, HttpServletRequest request) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.userCheckInExportToEmail(param, token, request);
    }

    /**
     * 用户提现
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/user/withdrawal")
    public Result userWithdrawal(@Validated @RequestBody UserWithdrawalParam param, BindingResult result,
                                 @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.userWithdrawal(param, token);
    }

    /**
     * 获取用户提现详情
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/user/withdrawal/info")
    public Result userWithdrawalInfo(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.userWithdrawalInfo(param);
    }

    /**
     * 用户提现数据导出到邮箱
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/user/withdrawal/export")
    public Result userWithdrawalExportToEmail(@Validated @RequestBody UserCoinLogParam param, BindingResult result,
                                              @RequestHeader("token") String token, HttpServletRequest request) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.userWithdrawalExportToEmail(param, token, request);
    }

    /**
     * 获取币变更日志列表
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/coin/log/lists")
    public Result coinLogLists(@Validated @RequestBody UserCoinLogParam param, BindingResult result,
                               @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.coinLogLists(param, token);
    }

    /**
     * 获取用户打卡排行榜列表
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/user/check_in/rank/lists")
    public Result userCheckInRankLists(@Validated @RequestBody SearchParam param, BindingResult result,
                                       @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.userCheckInRankLists(param, token);
    }

    /**
     * 获取用户提现排行榜列表
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/user/withdrawal/rank/lists")
    public Result userWithdrawalRankLists(@Validated @RequestBody SearchParam param, BindingResult result,
                                          @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.userWithdrawalRankLists(param, token);
    }

    /**
     * 导出用户打卡排行榜（月榜）
     *
     * @param param
     * @param result
     * @param token
     * @param response
     */
    @RequestMapping("/user/check_in/rank/export")
    public void userCheckInRankExport(@Validated @RequestBody SearchParam param, BindingResult result,
                                      @RequestHeader("token") String token, HttpServletResponse response) {
        if (result.hasErrors()) {
            throw new IllegalArgumentException(result.getFieldError().getDefaultMessage());
        }
        service.userCheckInRankExport(param, token, response);
    }

    /**
     * 获取商品分类列表
     *
     * @return
     */
    @RequestMapping("/product/category/lists")
    public Result productCategoryLists(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return categoryService.selectList(param);
    }

    /**
     * 获取商品列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/product/lists")
    public Result productLists(@Validated @RequestBody ProductSearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        param.setSearchStatusList(Arrays.asList(1, 3));
        param.setLimitTime(LocalDateTime.now());
        return productService.selectList(param);
    }

    /**
     * 获取商品详情
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/product/info")
    public Result productInfo(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return productService.getProduct(param);
    }

    /**
     * 商品兑换
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/product/exchange")
    public Result productExchange(@Validated @RequestBody ProductExchangeParam param, BindingResult result,
                                  @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.productExchange(param, token);
    }

    /**
     * 获取商品订单支付配置信息
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/product/exchange/order/pay/config")
    public Result productExchangeOrderPayConfig(@Validated @RequestBody ProductExchangeOrderPayParam param,
                                                BindingResult result,
                                                @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.productExchangeOrderPayConfig(param, token);
    }

    /**
     * 商品订单支付回调
     *
     * @param param
     * @return
     */
    @RequestMapping("/product/exchange/order/pay/callback")
    public String productExchangeOrderPayCallback(@RequestBody WxJsPayCallBackResponse param) {
        return service.productExchangeOrderPayCallback(param);
    }

    /**
     * 获取商品兑换列表
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/product/exchange/lists")
    public Result productExchangeLists(@Validated @RequestBody SearchParam param, BindingResult result,
                                       @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.productExchangeLists(param, token);
    }

    /**
     * 获取商品兑换详情
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/product/exchange/info")
    public Result productExchangeInfo(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.productExchangeInfo(param);
    }

    /**
     * 商品兑换退货申请
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/product/exchange/refund")
    public Result productExchangeRefund(@Validated @RequestBody ProductExchangeRefundParam param, BindingResult result,
                                        @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.productExchangeRefund(param, token);
    }

    /**
     * 兑换券码核销
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/product/exchange/ticket/check")
    public Result productExchangeTicketCheck(@Validated @RequestBody SearchParam param, BindingResult result,
                                             @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.productExchangeTicketCheck(param, token);
    }

    /**
     * 获取场所打卡记录列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/place/check_in/list")
    public Result placeCheckInList(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.placeCheckInList(param);
    }


}
