package com.zemcho.guzhe.controller.wechat.shop;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.merchant.param.MerchantSaveParam;
import com.zemcho.guzhe.controller.merchant.param.UploadMerchantImageParam;
import com.zemcho.guzhe.entity.merchant.Merchant;
import com.zemcho.guzhe.service.merchant.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wechat/merchant")
public class ShopMerchantController {
    @Autowired
    private MerchantService merchantService;

    /**
     * 商户图片上传
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/image/upload")
    public Result uploadMerchantImage(@Validated @RequestBody UploadMerchantImageParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return merchantService.uploadMerchantImage(param);
    }

    /**
     * 添加商户
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/add")
    public Result add(@Validated @RequestBody MerchantSaveParam param, BindingResult result,
                      @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return merchantService.add(param, token, true);
    }

    /**
     * 修改商户
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/update")
    public Result update(@Validated @RequestBody MerchantSaveParam param, BindingResult result,
                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return merchantService.update(param, token, true);
    }

    /**
     * 获取商户信息
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/info")
    public Result getMerchantInfo(@Validated @RequestBody SearchParam param, BindingResult result,
                                  @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        Integer id = param.getSearchId();
        Integer shopId = param.getSearchField1();
        if (id == null || shopId == null) {
            return Result.error("参数异常");
        }

        return merchantService.selectById(id, token, true, shopId);
    }
}
