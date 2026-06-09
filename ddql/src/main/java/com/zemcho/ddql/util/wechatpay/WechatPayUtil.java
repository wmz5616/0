package com.zemcho.ddql.util.wechatpay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.wechat.pay.java.core.cipher.Signer;
import com.wechat.pay.java.service.certificate.CertificateService;
import com.wechat.pay.java.service.refund.model.QueryByOutRefundNoRequest;
import com.zemcho.ddql.config.wechat.WechatPayConfig;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.cipher.PrivacyDecryptor;
import com.wechat.pay.java.core.exception.HttpException;
import com.wechat.pay.java.core.exception.MalformedMessageException;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.http.*;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import com.zemcho.ddql.util.wechatpay.dto.InitiateBatchTransferRequestNew;
import com.zemcho.ddql.util.wechatpay.dto.InitiateBatchTransferResponseNew;
import com.zemcho.ddql.util.wechatpay.dto.TransferDetailEntityNew;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 微信支付工具类
 */
@Component
@Slf4j
public class WechatPayUtil {
    @Autowired
    private WechatPayConfig wechatPayConfig;

    @Autowired
    private Config config;

    private JsapiService jsapiService;

    private RefundService refundService;

    private CertificateService certificateService;

    @Autowired
    public void initService() {
        this.jsapiService = new JsapiService.Builder().config(config).build();
        this.refundService = new RefundService.Builder().config(config).build();
        this.certificateService = new CertificateService.Builder().config(config).build();
    }

    /**
     * JSAPI 支付
     *
     * @param outTradeNo  商户订单号
     * @param description 商品描述
     * @param total       总金额（分）
     * @param openid      用户openid
     * @param notifyUrl   支付通知地址
     * @return 支付参数
     */
    public Map<String, String> jsapiPay(String outTradeNo, String description, Integer total,
                                        String openid, String notifyUrl) {
        try {
            if (!wechatPayConfig.getPayStatus()) {
                total = 1;
            }

            PrepayRequest request = new PrepayRequest();
            Amount amount = new Amount();
            amount.setTotal(total);
            request.setAmount(amount);
            request.setAppid(wechatPayConfig.getAppId());
            request.setMchid(wechatPayConfig.getMchId());
            request.setDescription(description);
            request.setNotifyUrl(notifyUrl);
            request.setOutTradeNo(outTradeNo);
            Payer payer = new Payer();
            payer.setOpenid(openid);
            request.setPayer(payer);

            PrepayResponse response = jsapiService.prepay(request);

            // 构建前端支付参数
            return buildPayParams(response.getPrepayId());
        } catch (HttpException e) {
            log.error("微信支付HTTP异常: message={}", e.getMessage());
            return null;
        } catch (ServiceException e) {
            log.error("微信支付服务异常: errorCode={}, errorMessage={}", e.getErrorCode(), e.getErrorMessage());
            return null;
        } catch (MalformedMessageException e) {
            log.error("微信支付响应解析异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 构建前端支付参数
     */
    private Map<String, String> buildPayParams(String prepayId) {
        Map<String, String> payParams = new HashMap<>();
        String appId = wechatPayConfig.getAppId();

        // 构造参数
        payParams.put("appId", appId);
        payParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        payParams.put("nonceStr", generateNonceStr());
        payParams.put("package", "prepay_id=" + prepayId);
        payParams.put("signType", "RSA");

        try {
            // 使用 config 创建 Signer
            Signer signer = config.createSigner();
            String message = buildSignMessage(payParams);
            String paySign = signer.sign(message).getSign();
            payParams.put("paySign", paySign);

            log.info("JSAPI 支付参数已签名: appId={}, prepayId={}, paySign={}", appId, prepayId, paySign);

            return payParams;
        } catch (Exception e) {
            log.error("生成 JSAPI 支付签名失败", e);
            return null;
        }
    }

    /**
     * 构造签名原文：appId\n时间戳\n随机字符串\nprepay_id\n
     */
    private String buildSignMessage(Map<String, String> params) {
        return params.get("appId") + "\n" +
                params.get("timeStamp") + "\n" +
                params.get("nonceStr") + "\n" +
                params.get("package") + "\n";
    }

    /**
     * 生成随机字符串
     */
    private String generateNonceStr() {
        return String.valueOf(System.currentTimeMillis()) + (int) (Math.random() * 1000);
    }

    /**
     * 查询订单
     *
     * @param transactionId 微信支付订单号
     * @param outTradeNo    商户订单号
     * @return 订单信息
     */
    public Transaction queryOrder(String transactionId, String outTradeNo) {
        try {
            if (transactionId != null && !transactionId.trim().isEmpty()) {
                QueryOrderByIdRequest request = new QueryOrderByIdRequest();
                request.setTransactionId(transactionId);
                return jsapiService.queryOrderById(request);
            } else {
                QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
                request.setMchid(wechatPayConfig.getMchId());
                request.setOutTradeNo(outTradeNo);
                return jsapiService.queryOrderByOutTradeNo(request);
            }
        } catch (Exception e) {
            log.error("查询订单异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 关闭订单
     *
     * @param outTradeNo 商户订单号
     */
    public void closeOrder(String outTradeNo) {
        try {
            CloseOrderRequest request = new CloseOrderRequest();
            request.setMchid(wechatPayConfig.getMchId());
            request.setOutTradeNo(outTradeNo);
            jsapiService.closeOrder(request);
        } catch (Exception e) {
            log.error("关闭订单异常: {}", e.getMessage());
            return;
        }
    }

    /**
     * 申请退款
     *
     * @param outTradeNo  商户订单号
     * @param outRefundNo 商户退款单号
     * @param total       原订单金额（分）
     * @param refund      退款金额（分）
     * @param notifyUrl   退款通知地址
     * @return 退款结果
     */
    public Refund refund(String outTradeNo, String outRefundNo, Integer total, Integer refund, String notifyUrl) {
        try {
            if (!wechatPayConfig.getPayStatus()) {
                total = 1;
                refund = 1;
            }

            CreateRequest request = new CreateRequest();
            request.setOutTradeNo(outTradeNo);
            request.setOutRefundNo(outRefundNo);
            request.setNotifyUrl(notifyUrl);

            AmountReq amountReq = new AmountReq();
            amountReq.setRefund(Long.valueOf(refund));
            amountReq.setTotal(Long.valueOf(total));
            amountReq.setCurrency("CNY");
            request.setAmount(amountReq);

            Refund result = refundService.create(request);
            log.info("微信申请退款 request : {} result : {}", request, result);

            return result;
        } catch (HttpException e) {
            log.error("微信退款HTTP异常: message={}", e.getMessage());
            return null;
        } catch (ServiceException e) {
            log.error("微信退款服务异常: errorCode={}, errorMessage={}", e.getErrorCode(), e.getErrorMessage());
            return null;
        } catch (MalformedMessageException e) {
            log.error("微信退款响应解析异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 查询退款
     *
     * @param outRefundNo 商户退款单号
     * @return 退款信息
     */
    public Refund queryRefund(String outRefundNo) {
        try {
            QueryByOutRefundNoRequest request = new QueryByOutRefundNoRequest();
            request.setOutRefundNo(outRefundNo);
            return refundService.queryByOutRefundNo(request);
        } catch (Exception e) {
            log.error("查询退款异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 商家转账 - 发起转账 - 2025年1月15号之后，商户转账零线必须用户确认收款
     *
     * @param request 请求体
     * @return
     */
    public InitiateBatchTransferResponseNew initiateBatchTransferNew(InitiateBatchTransferRequestNew request) {
        try {
            if (!wechatPayConfig.getPayStatus()) {
                request.setTransferAmount(1);
            }

            log.info("WxPayService.initiateBatchTransferNew request:{}", request.toString());
            String encryptName = config.createEncryptor().encrypt(request.getUserName());
            request.setUserName(encryptName);
            String requestPath = "https://api.mch.weixin.qq.com/v3/fund-app/mch-transfer/transfer-bills";
            HttpHeaders headers = new HttpHeaders();
            headers.addHeader("Accept", MediaType.APPLICATION_JSON.getValue());
            headers.addHeader("Content-Type", MediaType.APPLICATION_JSON.getValue());
            headers.addHeader("Wechatpay-Serial", config.createEncryptor().getWechatpaySerial());
            HttpRequest httpRequest =
                    new HttpRequest.Builder()
                            .httpMethod(HttpMethod.POST)
                            .url(requestPath)
                            .headers(headers)
                            .body(createRequestBody(request))
                            .build();
            HttpClient httpClient = new DefaultHttpClientBuilder().config(config).build();
            HttpResponse<InitiateBatchTransferResponseNew> httpResponse = httpClient.execute(httpRequest,
                    InitiateBatchTransferResponseNew.class);
            log.info("WxPayService.initiateBatchTransferNew response:{}", httpResponse.getServiceResponse());
            return httpResponse.getServiceResponse();
        } catch (Exception e) {
            log.error("WxPayService.initiateBatchTransferNew Error : {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 商家转账 - 商户单号查询转账单 - 2025年1月15号之后，商家转账用户确认模式下，根据商户单号查询转账单的详细信息
     *
     * @param outBillNo 商户系统内部的商家单号
     * @return
     */
    public TransferDetailEntityNew getTransferDetailByOutNoNew(String outBillNo) {
        try {
            log.info("WxPayService.getTransferDetailByOutNoNew request:{}", outBillNo);
            String requestPath = "https://api.mch.weixin.qq.com/v3/fund-app/mch-transfer/transfer-bills/out-bill-no" +
                    "/{out_bill_no}";
            requestPath = requestPath.replace("{out_bill_no}", UrlEncoder.urlEncode(outBillNo));
            HttpHeaders headers = new HttpHeaders();
            headers.addHeader("Accept", MediaType.APPLICATION_JSON.getValue());
            headers.addHeader("Content-Type", MediaType.APPLICATION_JSON.getValue());
            HttpRequest httpRequest =
                    new HttpRequest.Builder()
                            .httpMethod(HttpMethod.GET)
                            .url(requestPath)
                            .headers(headers)
                            .build();
            PrivacyDecryptor decryptor = config.createDecryptor();
            HttpClient httpClient = new DefaultHttpClientBuilder().config(config).build();
            HttpResponse<TransferDetailEntityNew> httpResponse = httpClient.execute(httpRequest,
                    TransferDetailEntityNew.class);
            log.info("WxPayService.getTransferDetailByOutNoNew response:{}", httpResponse.getServiceResponse());
            return httpResponse.getServiceResponse().cloneWithCipher(decryptor);
        } catch (Exception e) {
            log.error("WxPayService.getTransferDetailByOutNoNew Error : {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 转json
     *
     * @param request 请求体
     * @return
     */
    private static RequestBody createRequestBody(Object request) {
        return new JsonRequestBody.Builder().body(new Gson().toJson(request)).build();
    }

    /**
     * 微信回调通知-验签并返回对应的参数信息
     *
     * @return
     */
    public <T> T wxNotifyCallback(String requestBody, Map<String, String> headers, Class<T> clazz) {
//        String requestBody = getBodyString(request, "UTF-8");
        //证书序列号（微信平台）   验签的“微信支付平台证书”所对应的平台证书序列号
//        String wechatPaySerial = request.getHeader("Wechatpay-Serial");
        String wechatPaySerial = headers.get("wechatpay-serial");
        //微信传递过来的签名   验签的签名值
//        String wechatSignature = request.getHeader("Wechatpay-Signature");
        String wechatSignature = headers.get("wechatpay-signature");
        //验签的时间戳
//        String wechatTimestamp = request.getHeader("Wechatpay-Timestamp");
        String wechatTimestamp = headers.get("wechatpay-timestamp");
        //验签的随机字符串
//        String wechatpayNonce = request.getHeader("Wechatpay-Nonce");
        String wechatpayNonce = headers.get("wechatpay-nonce");

        // 1. 构造 RequestParam
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(wechatPaySerial)
                .nonce(wechatpayNonce)
                .signature(wechatSignature)
                .timestamp(wechatTimestamp)
                .body(requestBody)
                .build();

        log.info("WxPayService.wxNotifyCallback request : wechatPaySerial is [{}]  , wechatSignature is [{}] , " +
                        "wechatTimestamp is [{}] , wechatpayNonce  is [{}] , requestBody is [{}]", wechatPaySerial,
                wechatSignature, wechatTimestamp, wechatpayNonce, requestBody);

        // 3. 初始化 NotificationParser
        NotificationConfig notificationConfig = (NotificationConfig) config;
//        log.info("WxPayService.wxNotifyCallback getSignType : {}, getCipherType : {} getSerialNumber : {}",
//                notificationConfig.getSignType(), notificationConfig.getCipherType(),
//                notificationConfig.createVerifier().getSerialNumber());
        NotificationParser parser = new NotificationParser(notificationConfig);
        try {
            T entity = parser.parse(requestParam, clazz);
            log.info("WxPayService.wxNotifyCallback responseBody: {}", entity != null ?
                    JSON.toJSONString(entity) : null);
            return entity;
        } catch (ValidationException e) {
            log.error("签名验证失败", e);
            return null;
        } catch (Exception e) {
            log.error("系统内部错误", e);
            return null;
        }
    }

    /**
     * 获取post请求中的Body
     *
     * @param request httpRequest
     * @return body字符串
     */
    public static String getBodyString(HttpServletRequest request, String charSet) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = request.getInputStream();
            //读取流并将流写出去,避免数据流中断;
            reader = new BufferedReader(new InputStreamReader(inputStream, charSet));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            inputStream.close();
            reader.close();
        } catch (IOException e) {
            log.error("获取requestBody异常", e);
        }

        return sb.toString();
    }

    /**
     * 微信回调成功返回
     *
     * @param data
     * @return
     */
    public ResponseEntity<String> wxNotifySuccessResponse(Map<String, Object> data) {
        log.info("WxPayService.wxNotifySuccessResponse data : {}", data);
        return ResponseEntity.ok().body(JSON.toJSONString(data));
    }

    /**
     * 微信回调失败返回
     *
     * @param data
     * @return
     */
    public ResponseEntity<String> wxNotifyFailResponse(Map<String, Object> data) {
        log.info("WxPayService.wxNotifyFailResponse data : {}", data);
        return ResponseEntity.status(500).body(JSON.toJSONString(data));
    }
}
