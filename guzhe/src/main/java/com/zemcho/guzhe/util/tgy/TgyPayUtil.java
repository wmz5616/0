package com.zemcho.guzhe.util.tgy;

import com.alibaba.fastjson.JSONObject;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.config.tgy_pay.MerchantConfig;
import com.zemcho.guzhe.util.BigDecimalUtil;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.HttpUtil;
import com.zemcho.guzhe.util.tgy.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @title: TgyPayUtil
 * @Description: 通莞支付工具类
 * @Date: 2026/3/10 16:39
 */
@Component
public class TgyPayUtil {
    private static Logger logger = LoggerFactory.getLogger(TgyPayUtil.class);

    @Autowired
    private MerchantConfig merchantConfig;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 获取微信支付配置信息
     *
     * @param openId
     * @param amount
     * @param orderNo
     * @param body
     * @param notifyUrl
     * @param isDivide
     * @return
     */
    public Result getWxJsPayConfig(String openId, Integer amount, String orderNo, String body,
                                   String notifyUrl, Boolean isDivide) {
        try {
            String payInfoTemp = redisTemplate.opsForValue().get(Constant.WECHAT_ORDER_PREFIX + orderNo);
            if (payInfoTemp != null && !payInfoTemp.isEmpty()) {
                return Result.success("获取成功", JSONObject.parseObject(payInfoTemp, Map.class));
            }

            // 将以分为单位的金额转为元
            Double payMoney = BigDecimalUtil.divisionDouble(amount, 100, 2);

            // 开始设置请求的参数
            WxJsPayJson request = new WxJsPayJson();
            request.setAccount(merchantConfig.getAccount());
            request.setPayMoney(payMoney);
            request.setLowOrderId(orderNo);
//            request.setIsUniqueLowOrderId("1");
            request.setBody(body);
            request.setNotifyUrl(notifyUrl);
            request.setReturnUrl("");
            request.setAppId(merchantConfig.getAppId());
            request.setOpenId(openId);
            request.setIsMinipg("1");
            request.setProfitSharing("Y");
            if (isDivide) { //要分账的订单需要传
                request.setFundProcessType("DELAY_SETTLE");
            }

            // 最后再签名
            String sign = SignUtil.generateSignNew(request, merchantConfig.getKey());
            request.setSign(sign);

            // 请求体
            String data = JSONObject.toJSONString(request);
            // 发送请求
            String response = HttpUtil.sendPost(merchantConfig.getWxJsPayURL(), data, null);
            logger.info("请求获取支付配置信息 param : {} , result : {}", data, response);
            // 结果解析成对象
            WxJsPayResponse wxJsPayResponse = JSONObject.parseObject(response, WxJsPayResponse.class);
            if (wxJsPayResponse == null || !wxJsPayResponse.getStatus().equals(100)) {
                throw new Exception("请求获取支付配置信息失败");
            }
            // 验签
            boolean verifySign = SignUtil.verifySign(merchantConfig.getKey(), wxJsPayResponse.getSign(),
                    wxJsPayResponse);
            if (!verifySign) {
                throw new Exception("请求获取支付配置信息-验签失败");
            }

            // 支付的配置信息
            String payInfo = wxJsPayResponse.getPay_info();

            // 将支付的配置信息保存到Redis中 过期时间是15分钟
            Map<String, Object> temp = JSONObject.parseObject(payInfo, Map.class);
            // 获取支付的过期时间戳 以秒为单位
            Long endTimeStamp = Long.valueOf((String) temp.get("timeStamp"));
            // 获取当前时间并转成以秒为单位的时间戳
            Long nowTimeStamp = System.currentTimeMillis() / 1000;
            Long expireTime = endTimeStamp - nowTimeStamp;
            // 额外减去120秒
            expireTime = expireTime - 120;
            if (expireTime <= 0) {
                expireTime = 120L;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("upOrderId", wxJsPayResponse.getUpOrderId());
            result.put("payInfo", temp);

            redisTemplate.opsForValue().set(Constant.WECHAT_ORDER_PREFIX + orderNo, JSONObject.toJSONString(result),
                    expireTime, TimeUnit.SECONDS);

            return Result.success("获取成功", result);
        } catch (Exception e) {
            logger.error("获取支付配置信息失败", e);
            return Result.error("获取支付配置信息失败，" + e.getMessage());
        }
    }

    /**
     * 退款
     *
     * @param upOrderId
     * @param refundAmount
     * @return
     */
    public Result refund(String upOrderId, Integer refundAmount) {
        try {
            // 将以分为单位的金额转为元
            Double refundMoney = BigDecimalUtil.divisionDouble(refundAmount, 100, 2);

            WxJsRefundJson wxJsRefundJson = new WxJsRefundJson();
            wxJsRefundJson.setAccount(merchantConfig.getAccount());
            wxJsRefundJson.setUpOrderId(upOrderId);
            wxJsRefundJson.setRefundMoney(refundMoney);
            String sign = SignUtil.generateSignNew(wxJsRefundJson, merchantConfig.getKey());
            wxJsRefundJson.setSign(sign);

            // 请求体
            String data = JSONObject.toJSONString(wxJsRefundJson);
            // 发送请求
            String response = HttpUtil.sendPost(merchantConfig.getWxJsRefundURL(), data, null);
            logger.info("请求通莞退款 param : {} , result : {}", data, response);
            // 结果解析成对象
            WxJsRefundResponse wxJsRefundResponse = JSONObject.parseObject(response, WxJsRefundResponse.class);
            if (wxJsRefundResponse == null || !wxJsRefundResponse.getStatus().equals(100)) {
                throw new Exception("请求通莞退款失败");
            }

            return Result.success("请求退款成功", wxJsRefundResponse);
        } catch (Exception e) {
            logger.error("通莞退款失败", e);
            return Result.error("通莞退款失败，" + e.getMessage());
        }
    }

    /**
     * 分账
     *
     * @param divideData
     * @return
     */
    public Result divide(PayDivideDto divideData) {
        try {
            String sign = SignUtil.generateSignNew(divideData, merchantConfig.getKey());
            divideData.setSign(sign);

            // 请求体
            String data = JSONObject.toJSONString(divideData);
            // 发送请求
            String response = HttpUtil.sendPost(merchantConfig.getPayDivideURL(), data, null);
            logger.info("请求通莞分账 param : {} , result : {}", data, response);
            // 结果解析成对象
            Map<String, Object> resultMap = JSONObject.parseObject(response, Map.class);
            if (resultMap == null) {
                throw new Exception("请求通莞分账失败");
            }
            if (!resultMap.get("status").equals(100)) {
                throw new Exception("请求通莞分账失败，" + resultMap.get("message"));
            }

            return Result.success("请求通莞分账成功", resultMap);
        } catch (Exception e) {
            logger.error("通莞分账失败", e);
            return Result.error("通莞分账失败，" + e.getMessage());
        }
    }

    /**
     * 查询商户进件结果
     *
     * @param requestNo
     * @return
     */
    public Result queryMerchantChannel(String requestNo) {
        try {
            MerchantQueryRequestJson merchantQueryRequestJson = new MerchantQueryRequestJson(requestNo,
                    merchantConfig.getLoginAccount(), null);
            // 签名
            String sign = SignUtil.generateSignNew(merchantQueryRequestJson,
                    merchantConfig.getParentChannelMerchantKey());
            merchantQueryRequestJson.setSign(sign);

            // 请求体
            String data = JSONObject.toJSONString(merchantQueryRequestJson);
            // 发送请求
            String response = HttpUtil.sendPost(merchantConfig.getQueryMerchantUrl(), data, null);
            MerchantRequestResponse queryResponse = JSONObject.parseObject(response, MerchantRequestResponse.class);
            logger.info("请求通莞进件结果查询 param : {} , result : {}", data, response);

            if (queryResponse == null || !queryResponse.getCode().equals("200")) {
                throw new Exception("请求进件结果查询失败");
            }

            return Result.success("进件结果查询成功", queryResponse.getData());
        } catch (Exception e) {
            logger.error("进件结果查询失败", e);
            return Result.error("进件结果查询失败，" + e.getMessage());
        }
    }

    /**
     * 上传商户图片
     *
     * @param fileStr
     * @return
     */
    public Result uploadMerchantImage(String fileStr) {
        try {
            ImageUploadJson imageUploadJson = new ImageUploadJson(fileStr, merchantConfig.getLoginAccount(), null);
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("fileStr", fileStr);
            requestBody.put("loginAccount", merchantConfig.getLoginAccount());
            requestBody.put("sign", SignUtil.generateSignNew(imageUploadJson,
                    merchantConfig.getParentChannelMerchantKey()));

            // 发送Post请求
            String response = HttpUtil.sendPost(merchantConfig.getUploadFileUrl(), requestBody, null);
            System.out.println("uploadMerchantImage sendResponse : " + response);

            // 解析响应结果
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (jsonObject == null) {
                return Result.error("上传文件失败");
            }
            if (!jsonObject.getInteger("code").equals(200)) {
                return Result.error("上传文件失败，" + jsonObject.getString("desc"));
            }
            String dataJsonString = jsonObject.getString("data");
            if (dataJsonString == null) {
                return Result.error("上传文件失败，获取上传结果失败");
            }
            JSONObject dataJsonObject = JSONObject.parseObject(dataJsonString);
            String cloudUrl = dataJsonObject.getString("cloudUrl");
            if (cloudUrl == null) {
                return Result.error("上传文件失败，获取上传结果失败!");
            }

            return Result.success("上传成功", cloudUrl);
        } catch (Exception e) {
            logger.error("上传商户图片失败", e);
            return Result.error("上传商户图片失败，" + e.getMessage());
        }
    }

    /**
     * 添加商户进件
     *
     * @param merchantRequestJson
     * @return
     */
    public Result addMerchantChannel(MerchantRequestJson merchantRequestJson) {
        try {
            merchantRequestJson.setLoginAccount(merchantConfig.getLoginAccount());
            String sign = SignUtil.generateSignNew(merchantRequestJson, merchantConfig.getParentChannelMerchantKey());
            merchantRequestJson.setSign(sign);

            // 请求体
            String data = JSONObject.toJSONString(merchantRequestJson);
            // 发送请求
            String response = HttpUtil.sendPost(merchantConfig.getAddMerchantUrl(), data, null);
            logger.info("添加商户进件 param : {} , result : {}", data, response);

            // 解析成对象
            MerchantRequestResponse addResponse = JSONObject.parseObject(response, MerchantRequestResponse.class);
            // 判断结果并返回
            if (addResponse == null) {
                return Result.error("请求商户进件失败!");
            }
            if (!addResponse.getCode().equals("200")) {
                return Result.error("请求商户进件失败，" + addResponse.getDesc());
            }
            if (!addResponse.getData().getReturnCode().equals("NIG00000")) {
                return Result.error("进件请求失败:" + addResponse.getData().getReturnMsg());
            }

            return Result.success("添加商户进件成功", addResponse.getData());
        } catch (Exception e) {
            logger.error("添加商户进件失败", e);
            return Result.error("添加商户进件失败，" + e.getMessage());
        }
    }
}
