package com.zemcho.ddql.controller.wechat.shop;

/**
 * @author HXH
 */

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.wechat.shop.param.ShopOrderCreateParam;
import com.zemcho.ddql.service.business.ShopService;
import com.zemcho.ddql.service.wechat.order.ShopOrderService;
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
import com.zemcho.ddql.common.Result;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 收款二维码控制器
 */
@RestController
@RequestMapping("/wechat/receipt/qrcode")
public class ReceiptQrCodeController {

    @Autowired
    private ShopOrderService shopOrderService;

    @Autowired
    private ShopService shopService;

    /**
     * 生成收款二维码
     */
    @RequestMapping("/generate")
    public Result generateReceiptQrCode(@Validated @RequestBody SearchParam param, BindingResult result,
                                        @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopService.generateQrCode(param,token,true);
    }

    /**
     * 下载收款二维码
     */
    @RequestMapping("/download")
    public ResponseEntity<byte[]> downloadReceiptQrCode(@Validated @RequestBody SearchParam param, BindingResult result,
                                                        @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Result serviceResult = shopService.downloadQrCode(param,token, true);

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

    /**
     * 创建门店扫码支付订单
     *
     * @param param 订单参数
     * @param result 参数校验结果
     * @param token token
     * @return result
     */
    @RequestMapping("/order/create")
    public Result createOrder(@Validated @RequestBody ShopOrderCreateParam param, BindingResult result,
                              @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return shopOrderService.createOrder(param, token);
    }

}
