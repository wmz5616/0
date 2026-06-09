package com.zemcho.ddql.controller.business;

import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.ChangeOneParam;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.param.ShopParam;
import com.zemcho.ddql.service.business.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/business/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 新增/编辑店铺
     * @return
     */
    @Log(description = "新增/编辑店铺", module = "商圈管理-新增/编辑店铺")
    @RequestMapping("/save")
    public Result saveShop(@Validated @RequestBody ShopParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.saveShop(param);
    }

    /**
     * 删除店铺
     * @param param
     * @return
     */
    @Log(description = "删除店铺", module = "商圈管理-删除店铺")
    @RequestMapping("/delete")
    public Result deleteShop(@Validated @RequestBody DeleteParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.deleteShop(param);
    }

    /**
     * 获取店铺列表
     */
    @RequestMapping("/lists")
    public Result selectList(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.selectList(param);
    }

    /**
     * 禁用/启用店铺
     */
    @Log(description = "禁用/启用店铺", module = "商圈管理-禁用/启用店铺")
    @RequestMapping("/status")
    public Result updateStatus(@Validated @RequestBody ChangeOneParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.updateStatus(param);
    }

    /**
     * 禁用/启用商家消费置顶
     */
    @Log(description = "禁用/启用消费置顶", module = "商圈管理-禁用/启用商家消费置顶")
    @RequestMapping("/topConsumption/status")
    public Result updateTopConsumptionStatus(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.updateTopConsumptionStatus(param);
    }

    /**
     * 修改商家收款配置
     */
    @Log(description = "修改商家收款", module = "商圈管理-修改商家收款")
    @RequestMapping("/receipt/status")
    public Result updateReceiptConfig(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.updateReceiptConfig(param);
    }

    /**
     * 修改商家合同照片
     */
    @Log(description = "修改商家合同照片", module = "商圈管理-修改商家合同照片")
    @RequestMapping("/contract")
    public Result updateContract(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.updateContract(param);
    }


    /**
     * 根据id查询店铺详情
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/selectById")
    public Result selectById(@Validated @RequestBody SearchParam param, BindingResult result,
                             @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.selectById(param,false,token);
    }

    /**
     * 取消注销商家
     */
    @RequestMapping("/shopStatus")
    public Result updateShopStatus(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.updateShopStatus(param);
    }

    /**
     * 生成店铺二维码
     */
    @RequestMapping("/generateQrCode")
    public Result generateQrCode(@Validated @RequestBody SearchParam param, BindingResult result,
                                 @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.generateQrCode(param,token,false);
    }

    /**
     * 下载店铺二维码
     */
    @RequestMapping("/downloadQrCode")
    public ResponseEntity<byte[]> downloadQrCode(@Validated @RequestBody SearchParam param, BindingResult result,
                                                 @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Result serviceResult = shopService.downloadQrCode(param,token,false);

        if (!serviceResult.success()) {
            return ResponseEntity.status(400).build();
        }

        Map<String, Object> data = (Map<String, Object>) serviceResult.getData();
        byte[] imageBytes = (byte[]) data.get("imageBytes");
        String fileName = (String) data.get("fileName");

        try {
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", encodedFileName);
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

}
