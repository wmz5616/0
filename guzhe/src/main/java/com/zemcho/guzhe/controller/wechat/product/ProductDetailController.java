package com.zemcho.guzhe.controller.wechat.product;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.product.param.ProductParam;
import com.zemcho.guzhe.controller.product.param.StockParam;
import com.zemcho.guzhe.service.wechat.product.ProductDetailService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        return productDetailService.selectDetail(param, token, true);
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
        return productDetailService.saveProduct(param, token, true);
    }
    /**
     * 更改库存
     */
    @RequestMapping("/updateStock")
    public Result updateStock(@Validated @RequestBody StockParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return productDetailService.updateStock(param,true);
    }

    /**
     * 导入券码
     *
     * @return
     */
    @PostMapping("/ticket/import")
    public Result importTicket(@RequestParam("file") MultipartFile file, @RequestParam Integer productId) {
        return productDetailService.importTicket(file, productId, true);
    }

    /**
     * 导出券码
     *
     * @return
     */
    @PostMapping("/ticket/export")
    public void exportEquipment(@Validated @RequestBody SearchParam param, HttpServletResponse response) {
        // 根据placeId(searchId)查设备
        productDetailService.exportTicket(param, response,true);
    }

}
