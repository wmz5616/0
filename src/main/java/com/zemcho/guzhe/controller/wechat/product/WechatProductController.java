package com.zemcho.guzhe.controller.wechat.product;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.ChangeParam;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.product.param.ProductSearchParam;
import com.zemcho.guzhe.service.wechat.product.WeChatProductService;
import jakarta.validation.Valid;
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
//商品管理
@RestController
@RequestMapping("/wechat/product")
public class WechatProductController {
    @Autowired
    private WeChatProductService weChatProductService;


    /**
     * 查询商品分类列表
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/listCategory")
    public Result listCategory(@Validated @RequestBody SearchParam param, BindingResult result,
                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return weChatProductService.listCategory(param, token);
    }
    /**
     * 查询商品列表
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/select")
    public Result select(@Validated @RequestBody ProductSearchParam param, BindingResult result,
                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return weChatProductService.select(param, token);
    }

    /**
     * 根据分类id查询商品
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/category/select")
    public Result selectByCategoryId(@Validated @RequestBody SearchParam param, BindingResult result,
                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return weChatProductService.selectByCategoryId(param, token);
    }
    /**
     * 删除商品
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(@Valid @RequestBody DeleteParam param, BindingResult result
            ,@RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return weChatProductService.delete(param, token);
    }

    /**
     * 修改商品上架状态
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/update/status")
    public Result updateStatus(@Valid @RequestBody ChangeParam param, BindingResult result
            , @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return weChatProductService.updateStatus(param, token);
    }


}
