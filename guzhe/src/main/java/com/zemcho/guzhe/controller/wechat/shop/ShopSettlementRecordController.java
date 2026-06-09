package com.zemcho.guzhe.controller.wechat.shop;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.wechat.shop.param.SettlementRecordSearchParam;
import com.zemcho.guzhe.service.wechat.shop.WechatShopSettlementRecordService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


@RestController
@RequestMapping("/wechat/shop/settlement_record")
public class ShopSettlementRecordController {

    @Autowired
    private WechatShopSettlementRecordService settlementRecordService;

    /**
     * 获取商家结算记录列表
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/lists")
    public Result settlementRecordLists(@Validated @RequestBody SettlementRecordSearchParam param, BindingResult result,
                                        @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return settlementRecordService.lists(param, token);
    }

    /**
     * 导出商家结算记录
     *
     * @param param
     * @param result
     * @param response
     * @param token
     */
    @RequestMapping("/export")
    public void settlementRecordExport(@Validated @RequestBody SettlementRecordSearchParam param, BindingResult result,
                                       HttpServletResponse response, @RequestHeader("token") String token) {
        settlementRecordService.export(param, token, response);
    }
}
