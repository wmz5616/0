package com.zemcho.guzhe.controller.product;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.product.param.ProductParam;
import com.zemcho.guzhe.controller.product.param.ProductSearchParam;
import com.zemcho.guzhe.controller.product.param.StockParam;
import com.zemcho.guzhe.service.product.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.zemcho.guzhe.common.Result;

/**
 * @author HXH
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 新增/编辑商品
     *
     * @return
     */
    @Log(description = "新增/编辑商品", module = "商品管理")
    @RequestMapping("/save")
    public Result saveProduct(@Validated @RequestBody ProductParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return productService.saveProduct(param);
    }

    /**
     * 获取商品列表
     *
     * @return
     */
    @RequestMapping("/lists")
    public Result selectList(@Validated @RequestBody ProductSearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return productService.selectList(param);
    }

    /**
     * 导出商品数据
     *
     * @param param
     * @param result
     * @param response
     */
    @RequestMapping("/export")
    public void orderExport(@Validated @RequestBody ProductSearchParam param, BindingResult result,
                            HttpServletResponse response) {
        productService.productDataExport(param, response);
    }

    /**
     * 根据id获取商品信息
     */
    @RequestMapping("/selectById")
    public Result getProduct(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return productService.getProduct(param);

    }

    /**
     * 根据id批量删除商品
     *
     * @return
     */
    @Log(description = "删除商品", module = "商品管理")
    @RequestMapping("/delete")
    public Result deleteProduct(@Validated @RequestBody DeleteParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return productService.deleteProduct(param);
    }

    /**
     * 更改库存
     */
    @Log(description = "更改库存", module = "商品管理")
    @RequestMapping("/updateStock")
    public Result updateStock(@Validated @RequestBody StockParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return productService.updateStock(param);
    }

    /**
     * 导出券码
     *
     * @return
     */
    @PostMapping("/ticket/export")
    public void exportEquipment(@Validated @RequestBody SearchParam param, HttpServletResponse response) {
        // 根据placeId(searchId)查设备
        productService.exportTicket(param, response);
    }

    /**
     * 导入券码
     *
     * @return
     */
    @PostMapping("/ticket/import")
    public Result importTicket(@RequestParam("file") MultipartFile file, @RequestParam Integer productId) {

        return productService.importTicket(file, productId);
    }
}
