package com.zemcho.guzhe.controller.wechat.product;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.product.param.ProductParam;
import com.zemcho.guzhe.service.wechat.product.ProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HXH
 */
//商品详情
@RestController
@RequestMapping("/wechat/product")
public class ProductDetailController {
    @Autowired
    private ProductDetailService productDetailService;
    /**
     * 查询商品详情
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/selectDetail")
    public Result selectDetail(@Validated @RequestBody SearchParam param, BindingResult result,
                               @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return productDetailService.selectDetail(param, token);
    }

    /**
     * 新增/编辑商品
     *
     * @return
     */
    @RequestMapping("/save")
    public Result saveProduct(@Validated @RequestBody ProductParam param, BindingResult result,
                              @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return productDetailService.saveProduct(param, token);
    }

}
