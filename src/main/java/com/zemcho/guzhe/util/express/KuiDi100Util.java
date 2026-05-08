package com.zemcho.guzhe.util.express;

import com.google.gson.Gson;
import com.kuaidi100.sdk.api.Subscribe;
import com.kuaidi100.sdk.contant.ApiInfoConstant;
import com.kuaidi100.sdk.core.IBaseClient;
import com.kuaidi100.sdk.pojo.HttpResult;
import com.kuaidi100.sdk.request.SubscribeParam;
import com.kuaidi100.sdk.request.SubscribeParameters;
import com.kuaidi100.sdk.request.SubscribeReq;
import com.kuaidi100.sdk.response.SubscribeResp;
import com.zemcho.guzhe.config.express.ExpressConfig;
import com.zemcho.guzhe.util.BeanUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @title: KuiDi100Util
 * @Description: 快递100工具类
 * @Date: 2025/10/20 18:54
 */
@Component
public class KuiDi100Util {
    private static final Logger log = LoggerFactory.getLogger(KuiDi100Util.class);

    private static ExpressConfig expressConfig;

    @PostConstruct
    public void initExpressConfig() {
        expressConfig = BeanUtil.getBean("expressConfig", ExpressConfig.class);
    }

    /**
     * 快递订阅
     *
     * @param companyCode
     * @param number
     * @param phone
     * @return
     */
    public static Boolean subscribe(String companyCode, String number, String phone) {
        try {
            SubscribeParameters subscribeParameters = new SubscribeParameters();
            subscribeParameters.setCallbackurl(expressConfig.getSubscribeCallback());
            subscribeParameters.setPhone(phone);

            SubscribeParam subscribeParam = new SubscribeParam();
            subscribeParam.setParameters(subscribeParameters);
            subscribeParam.setCompany(companyCode);
            subscribeParam.setNumber(number);
            subscribeParam.setKey(expressConfig.getKey());

            SubscribeReq subscribeReq = new SubscribeReq();
            subscribeReq.setSchema(ApiInfoConstant.SUBSCRIBE_SCHEMA);
            subscribeReq.setParam(new Gson().toJson(subscribeParam));

            IBaseClient subscribe = new Subscribe();
            HttpResult result = subscribe.execute(subscribeReq);
            if (result == null || result.getStatus() != 200) {
                log.error("快递100订阅失败: param: {} result : {}", subscribeParam, result);
                return false;
            }

            SubscribeResp subscribeResp = new Gson().fromJson(result.getBody(), SubscribeResp.class);
            if (!subscribeResp.isResult()) {
                log.error("快递100订阅失败2: param: {} result : {}", subscribeParam, subscribeResp);
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("快递100订阅异常", e);
            return false;
        }
    }
}
