package com.zemcho.guzhe.controller.merchant;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.merchant.param.MerchantSaveParam;
import com.zemcho.guzhe.controller.merchant.param.UploadMerchantImageParam;
import com.zemcho.guzhe.entity.merchant.Merchant;
import com.zemcho.guzhe.service.merchant.MerchantService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

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
     * @param param
     * @param result
     * @return
     */
    @Log(description = "添加商户", module = "商户管理")
    @RequestMapping("/add")
    public Result add(@Validated @RequestBody MerchantSaveParam param, BindingResult result,
                      @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return merchantService.add(param, token, false);
    }

    /**
     * @param param
     * @param result
     * @return
     */
    @Log(description = "修改商户", module = "商户管理")
    @RequestMapping("/update")
    public Result update(@Validated @RequestBody MerchantSaveParam param, BindingResult result,
                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return merchantService.update(param, token, false);
    }

    /**
     * @param merchant
     * @param result
     * @return
     */
    @Log(description = "删除商户", module = "商户管理")
    @RequestMapping("/delete")
    public Result delete(@Validated @RequestBody Merchant merchant, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return merchantService.delete(merchant.getId());
    }


    /**
     * @param param   keyword(merchant_name contact_phone legal_person)  searchType(main_type) searchField1(acc_type)
     *                searchIntStatus(status)
     * @param result
     * @param request
     * @return
     */
    @RequestMapping("/get/list")
    public Result select(@Validated @RequestBody SearchParam param, BindingResult result,
                         HttpServletRequest request) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return merchantService.select(param);
    }

    /**
     * @param merchant
     * @param result
     * @return
     */
    @RequestMapping("/get")
    public Result getById(@Validated @RequestBody Merchant merchant, BindingResult result,
                          @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        Integer id = merchant.getId();
        if (id == null) {
            return Result.error("参数异常");
        }
        return merchantService.selectById(id, token, false, null);
    }

    /**
     * 更改启用状态
     * @param param
     * @param result
     * @return
     */
    @Log(description = "更改启用状态", module = "商户管理")
    @RequestMapping("/status/set")
    public Result setStatus(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return merchantService.setStatus(param);
    }
}
