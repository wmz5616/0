package com.zemcho.guzhe.util.sms;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.config.sms.SmsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @title: SmsUtil
 * @Description:
 * @Date: 2025/7/4 8:57
 */
public class SmsUtil {
    private static Logger logger = LoggerFactory.getLogger(SmsUtil.class);

    /**
     * 发送短信
     *
     * @param phoneNumber
     * @param templateCode
     * @param templateParam
     * @return
     */
    public static Result send(String phoneNumber, String templateCode, String templateParam) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", SmsConfig.getAccessKeyId(),
                SmsConfig.getSecret());
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        // 接收短信的手机号码
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        // 短信签名名称。请在控制台签名管理页面签名名称一列查看（必须是已添加、并通过审核的短信签名）。
        request.putQueryParameter("SignName", SmsConfig.getSignName());
        // 短信模板ID
        request.putQueryParameter("TemplateCode", templateCode);
        // 短信模板变量对应的实际值，JSON格式。
        request.putQueryParameter("TemplateParam", templateParam);

        CommonResponse commonResponse = null;
        try {
            commonResponse = client.getCommonResponse(request);
        } catch (ClientException e) {
            logger.error("SmsUtil sendMessage error ", e);
            return new Result(10001, "短信发送失败！", e.getErrMsg());
        }

        String data = commonResponse.getData();
        JSONObject dataObject = JSONObject.parseObject(data);
        if ("OK".equals(dataObject.get("Code").toString())) {
            return Result.success("短信发送成功！", commonResponse.getData());
        }

        logger.error("SmsUtil sendMessage result : " + data);

        return Result.error("短信发送失败！", dataObject.get("Message").toString());
    }
}
